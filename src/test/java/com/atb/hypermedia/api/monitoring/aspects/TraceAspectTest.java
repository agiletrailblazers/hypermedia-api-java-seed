package com.atb.hypermedia.api.monitoring.aspects;

import com.atb.hypermedia.api.http.ManagedHttpClient;
import com.atb.hypermedia.api.monitoring.trace.RequestTracer;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraceAspectTest {

    @Mock
    private RequestTracer tracer;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @Mock
    private JoinPoint.StaticPart staticPart;

    // cannot mock a method, so we need to use a real method
    private Method methodWithArguments = ReflectionUtils
        .findMethod(this.getClass(), "sampleMethod", String.class, Integer.class, Long.class, String.class);
    private Method noArgMethod = ReflectionUtils.findMethod(this.getClass(), "methodWithNoArguments");

    @Mock
    private TraceRequest traceRequestAnnotation;

    @Mock
    private TraceMethod traceMethodAnnotation;

    @Mock
    private ManagedHttpClient httpClient;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    @InjectMocks
    private TraceAspect underTest = new TraceAspect();

    @Before
    public void setUp() throws Throwable {

        when(joinPoint.proceed()).thenReturn("SomeValue");
    }

    @Test
    public void testTraceRequests() throws Throwable {

        // The default trace settings will return typeName.methodName for the measurement key
        usingMethodWithNoArguments();
        when(traceRequestAnnotation.value()).thenReturn("TraceAspectTest.methodWithNoArguments");

        Object result = underTest.adviseTraceRequests(joinPoint, traceRequestAnnotation);
        assertThat(result).isInstanceOf(String.class).isEqualTo("SomeValue");

        verify(tracer, times(1)).startTracing("TraceAspectTest.methodWithNoArguments");
        verify(tracer, times(1)).stopTracing();
    }

    @Test(expected = Throwable.class)
    public void testTraceRequestCapturesEndEvenOnException() throws Throwable {

        usingMethodWithNoArguments();
        when(traceRequestAnnotation.value()).thenReturn("TraceAspectTest.methodWithNoArguments");
        when(joinPoint.proceed()).thenThrow(new RuntimeException());

        try {
            underTest.adviseTraceRequests(joinPoint, traceRequestAnnotation);
        } finally {
            verify(tracer, times(1)).addTimingMeasurement("TraceAspectTest.methodWithNoArguments:before");
            verify(tracer, times(1)).addTimingMeasurement("TraceAspectTest.methodWithNoArguments:after");
            verify(tracer, times(1)).startTracing("TraceAspectTest.methodWithNoArguments");
            verify(tracer, times(1)).stopTracing();
        }
    }

    @Test
    public void testTraceHttpClientExecute() throws Throwable {

        usingMethodWithNoArguments();
        when(traceMethodAnnotation.value()).thenReturn("TraceAspectTest.methodWithNoArguments");
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(joinPoint.proceed()).thenReturn(httpResponse);

        Object result = underTest.adviseHttpClientExecute(joinPoint, traceMethodAnnotation);

        assertThat(result).isNotNull();

        verify(tracer).startTiming("TraceAspectTest.methodWithNoArguments:httpCallDuration");
        verify(tracer).stopTiming("TraceAspectTest.methodWithNoArguments:httpCallDuration");
        verify(tracer).startTiming("TraceAspectTest.methodWithNoArguments:httpCallWithBodyDuration");
        verify(tracer).addTracingData("TraceAspectTest.methodWithNoArguments:statusCode", 200);
    }

    @Test(expected = Throwable.class)
    public void testTraceHttpClientExecuteCapturesEndEvenOnException() throws Throwable {

        usingMethodWithNoArguments();
        when(traceMethodAnnotation.value()).thenReturn("TraceAspectTest.methodWithNoArguments");
        when(joinPoint.proceed()).thenThrow(new RuntimeException());

        try {
            underTest.adviseHttpClientExecute(joinPoint, traceMethodAnnotation);
        } finally {
            verify(tracer).startTiming("TraceAspectTest.methodWithNoArguments:httpCallDuration");
            verify(tracer).stopTiming("TraceAspectTest.methodWithNoArguments:httpCallDuration");
            verify(tracer).startTiming("TraceAspectTest.methodWithNoArguments:httpCallWithBodyDuration");
            verify(tracer).startTiming("TraceAspectTest.methodWithNoArguments:processResponseDuration");
            verify(tracer).addTracingData("TraceAspectTest.methodWithNoArguments:statusCode", 200);
        }
    }

    @Test
    public void testTracedDataParametersAreAddedToTraceData() throws Throwable {

        usingMethodWithArugments();

        underTest.adviseTraceRequests(joinPoint, traceRequestAnnotation);

        verify(tracer).addTracingData("multipleAnnotationArgTraced", "bar");
        verify(tracer).addTracingData("singleAnnotationArgTraced", "juicey");
        verify(tracer, never()).addTracingData(Matchers.eq("noAnnotationArg"), Matchers.any());
        verify(tracer, never()).addTracingData(Matchers.eq("singleAnnotationArgNotTraced"), Matchers.any());
    }

    @Test
    public void testNoArgMethodDoesNotTraceData() throws Throwable {

        usingMethodWithNoArguments();

        underTest.adviseTraceRequests(joinPoint, traceRequestAnnotation);

        verify(tracer, never()).addTracingData(Matchers.anyString(), Matchers.any());
    }

    private void usingMethodWithNoArguments() {

        when(signature.getMethod()).thenReturn(noArgMethod);
        when(signature.getDeclaringType()).thenReturn(this.getClass());
        when(joinPoint.getStaticPart()).thenReturn(staticPart);
        when(staticPart.getSignature()).thenReturn(signature);
    }

    private void usingMethodWithArugments() {

        when(signature.getParameterNames()).thenReturn(sampleMethodArgNames);
        when(signature.getMethod()).thenReturn(methodWithArguments);
        when(signature.getDeclaringType()).thenReturn(this.getClass());
        when(joinPoint.getArgs()).thenReturn(sampleMethodArgValues);
        when(joinPoint.getStaticPart()).thenReturn(staticPart);
        when(staticPart.getSignature()).thenReturn(signature);
    }

    // It is essential that the argument names, values and annotations follow the same order
    // keeping all of them together here so we can make sure the ordering of everything aligns
    private String[] sampleMethodArgNames =
        { "multipleAnnotationArgTraced", "noAnnotationArg", "singleAnnotationArgNotTraced",
            "singleAnnotationArgTraced" };
    private Object[] sampleMethodArgValues = { "bar", 0, 100, "juicey" };

    public String sampleMethod(@TracedData @Nullable String multipleAnnotationArgTraced, Integer noAnnotationArg,
        @Nullable Long singleAnnotationArgNotTraced, @TracedData String singleAnnotationArgTraced) {

        return "SomeValue";
    }

    public void methodWithNoArguments() {

    }
}
