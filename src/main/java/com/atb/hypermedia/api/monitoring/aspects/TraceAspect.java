package com.atb.hypermedia.api.monitoring.aspects;

import com.atb.hypermedia.api.monitoring.trace.RequestTracer;
import com.atb.hypermedia.api.utilities.SpringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

@Aspect
public class TraceAspect {

    ///////////////////////////////////////////
    //          ASPECT DEFINITION           //
    /////////////////////////////////////////

    private static final String BEFORE = ":before";
    private static final String AFTER = ":after";
    private static final String STATUS_CODE = ":statusCode";
    private static final String BEFORE_HTTP = ":beforeHttpClientExecute";
    private static final String AFTER_HTTP = ":afterHttpClientExecute";

    // Captures the trace request annotation, makes sure it is only on controllers
    @Pointcut("execution(@com.atb.hypermedia.api.monitoring.aspects.TraceRequest * *(..)) && within(@org.springframework.stereotype.Controller *) && @annotation(traceRequestAnnotation)")
    public void traceRequest(TraceRequest traceRequestAnnotation) {

    }

    // For methods with the @TracedMethod annotation; also captures the annotation value
    @Pointcut("execution(@com.atb.hypermedia.api.monitoring.aspects.TraceMethod * *(..)) && @annotation(traceMethodAnnotation)")
    public void traceMethod(TraceMethod traceMethodAnnotation) {

    }

    // For HttpClient execute methods that return an Http Response
    @Pointcut("execution(public org.apache.http.HttpResponse com.atb.hypermedia.api.http.ManagedHttpClient.execute(..))")
    public void httpClientExecute() {

    }

    // Advise controller methods with the Trace Request annotation
    @Around("traceRequest(traceRequestAnnotation)")
    public Object adviseTraceRequests(ProceedingJoinPoint joinPoint, TraceRequest traceRequestAnnotation) throws Throwable {

        String measurementKey = traceRequestAnnotation.value();
        try {
            startTracing(measurementKey);
            traceMethodArguments(joinPoint);

            return joinPoint.proceed();
        } finally {
            stopTracing();
        }
    }

    // Advise non-controller methods that have the TraceMethod annotation
    @Around("traceMethod(traceMethodAnnotation)")
    public Object adviseTraceMethods(ProceedingJoinPoint joinPoint, TraceMethod traceMethodAnnotation) throws Throwable {

        String measurementKey = traceMethodAnnotation.value();
        try {
            if (traceMethodAnnotation.timed()) time(measurementKey + BEFORE);
            traceMethodArguments(joinPoint);

            return joinPoint.proceed();
        } finally {

            if (traceMethodAnnotation.timed()) time(measurementKey + AFTER);
        }
    }

    // Advise the HttpClient.execute method when called in the control flow of a method being traced
    @Around("httpClientExecute() && cflow(traceMethod(traceMethodAnnotation))")
    public Object adviseHttpClientExecute(ProceedingJoinPoint joinPoint, TraceMethod traceMethodAnnotation) throws Throwable {

        String measurementKey = traceMethodAnnotation.value();

        int statusCode = 0;
        try {
            time(measurementKey + BEFORE_HTTP);
            Object httpResponse = joinPoint.proceed();
            statusCode = getStatusCode(httpResponse);
            return httpResponse;
        } finally {
            traceData(measurementKey + STATUS_CODE, statusCode);
            time(measurementKey + AFTER_HTTP);
        }
    }

    ///////////////////////////////////////////
    //          ASPECT SUPPORT              //
    /////////////////////////////////////////

    private static final String NULL = "NULL";
    private static Logger logger = LoggerFactory.getLogger(TraceAspect.class);
    private RequestTracer requestTracer;
    private boolean requestTracerNotFound;

    // lazy loads the request tracer from the spring context
    // this is needed as we cannot wire up the aspect with spring
    private RequestTracer getRequestTracer() {

        if (requestTracer == null && !requestTracerNotFound) {

            requestTracer = SpringUtil.getBean(RequestTracer.class);
            if (requestTracer == null) {
                requestTracerNotFound = true;
                logger.warn("Request Tracer unable to be loaded in trace aspect; no tracing will take place");
            }
        }

        return requestTracer;
    }

    private int getStatusCode(Object possibleHttpResponse) {

        if (possibleHttpResponse != null) {
            HttpResponse response = (HttpResponse) possibleHttpResponse;
            if (response.getStatusLine() != null) {
                return response.getStatusLine().getStatusCode();
            }
        }
        return 0;
    }

    private void startTracing(String key) {

        if (getRequestTracer() != null)
            getRequestTracer().startTracing(key);
    }

    private void stopTracing() {

        if (getRequestTracer() != null)
            getRequestTracer().stopTracing();
    }

    private void time(String key) {

        if (getRequestTracer() != null)
            getRequestTracer().addTimingMeasurement(key);
    }

    private void traceData(String key, Object value) {

        if (getRequestTracer() != null)
            getRequestTracer().addTracingData(key, value);
    }

    private void traceMethodArguments(JoinPoint joinPoint) {

        if (methodHasArgumentsWithAnnotations(joinPoint)) {
            MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
            Annotation[][] annotationsByParam = signature.getMethod().getParameterAnnotations();

            for (int argIndex = 0; argIndex < annotationsByParam.length; argIndex++) {
                TracedData ann = tracedDataAnnotation(annotationsByParam[argIndex]);
                if (ann != null) {
                    Object arg = joinPoint.getArgs()[argIndex];
                    String argName = signature.getParameterNames()[argIndex];
                    String output = (arg == null) ? NULL : arg.toString();

                    traceData(argName, output);
                }
            }
        }
    }

    private TracedData tracedDataAnnotation(Annotation[] annotations) {

        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof TracedData) return (TracedData) annotation;
            }
        }
        return null;
    }

    private boolean methodHasArgumentsWithAnnotations(JoinPoint joinPoint) {

        if (joinPoint.getStaticPart().getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
            Annotation[][] annotations = signature.getMethod().getParameterAnnotations();
            return (ArrayUtils.isNotEmpty(annotations) && ArrayUtils.isNotEmpty(joinPoint.getArgs()));
        } else {
            return false;
        }
    }
}
