package com.atb.hypermedia.api.monitoring.trace;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.atb.hypermedia.api.monitoring.metrics.RequestTraceEmitter;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RequestTracer {
    private List<RequestTraceEmitter> emitters;
    private static final Logger logger = LoggerFactory.getLogger(RequestTracer.class);
    private static final ConcurrentMap<String, RequestTraceRecord> requestTraceMap = new ConcurrentHashMap();
    private long maxRequestsTraced = 1000L;

    public RequestTracer(List<RequestTraceEmitter> emitters) {
        this.emitters = emitters;
    }

    public void startTracing(String recordKey) {
        if ((long) requestTraceMap.size() < this.maxRequestsTraced) {
            long now = DateTimeUtils.currentTimeMillis();
            RequestTraceRecord requestTraceRecord = new RequestTraceRecord(recordKey, now);
            String fingerprint = MDC.get("FINGERPRINT");
            if (fingerprint != null) {
                requestTraceMap.put(fingerprint, requestTraceRecord);
            }
        }

    }

    public void addTimingMeasurement(String measurementKey) {
        String fingerprint = MDC.get("FINGERPRINT");
        if (fingerprint != null) {
            RequestTraceRecord traceRecord = (RequestTraceRecord) requestTraceMap.get(fingerprint);
            if (traceRecord != null) {
                long startTime = traceRecord.getStartTime();
                long now = DateTimeUtils.currentTimeMillis();
                traceRecord.addTimingMeasurement(measurementKey, now - startTime);
            }
        }

    }

    public void startTiming(String timerKey) {
        String fingerprint = MDC.get("FINGERPRINT");
        if (fingerprint != null) {
            RequestTraceRecord traceRecord = (RequestTraceRecord) requestTraceMap.get(fingerprint);
            if (traceRecord != null) {
                traceRecord.startTimer(timerKey);
            }
        }

    }

    public void stopTiming(String timerKey) {
        String fingerprint = MDC.get("FINGERPRINT");
        if (fingerprint != null) {
            RequestTraceRecord traceRecord = (RequestTraceRecord) requestTraceMap.get(fingerprint);
            if (traceRecord != null) {
                traceRecord.stopTimer(timerKey);
            }
        }

    }

    public void addTracingData(String key, Object value) {
        String fingerprint = MDC.get("FINGERPRINT");
        if (fingerprint != null) {
            RequestTraceRecord traceRecord = (RequestTraceRecord) requestTraceMap.get(fingerprint);
            if (traceRecord != null && value != null) {
                traceRecord.addTracingData(key, value.toString());
            }
        }

    }

    public void stopTracing() {
        String fingerprint = MDC.get("FINGERPRINT");
        if (fingerprint != null) {
            RequestTraceRecord traceRecord = (RequestTraceRecord) requestTraceMap.remove(fingerprint);
            if (traceRecord != null) {
                long startTime = traceRecord.getStartTime();
                long now = DateTimeUtils.currentTimeMillis();
                traceRecord.addTimingMeasurement("duration", now - startTime);
                Iterator i$ = this.emitters.iterator();

                while (i$.hasNext()) {
                    RequestTraceEmitter emitter = (RequestTraceEmitter) i$.next();
                    emitter.emit(traceRecord);
                }
            }
        }

    }

    public static RequestTraceRecord getRequestTraceRecord(String fingerprint) {
        return (RequestTraceRecord) requestTraceMap.get(fingerprint);
    }

    public static void removeTraceRecord(String fingerprint) {
        requestTraceMap.remove(fingerprint);
    }

    public static void clearAllTraceRecords() {
        requestTraceMap.clear();
    }
}