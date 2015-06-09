package com.atb.hypermedia.api.monitoring.trace;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTimeUtils;

public class RequestTraceRecord {
    private final String requestKey;
    private final long startTime;
    private final ConcurrentMap<String, Long> timingMeasurements;
    private final ConcurrentMap<String, String> tracingData;
    private final ConcurrentMap<String, Long> timers;

    public RequestTraceRecord(String requestKey, long startTime) {
        this.requestKey = requestKey;
        this.startTime = startTime;
        this.timingMeasurements = new ConcurrentHashMap();
        this.tracingData = new ConcurrentHashMap();
        this.timers = new ConcurrentHashMap();
    }

    public RequestTraceRecord(String requestKey, long startTime, ConcurrentMap<String, Long> timingMeasurements, ConcurrentMap<String, String> tracingData, ConcurrentMap<String, Long> timers) {
        this.requestKey = requestKey;
        this.startTime = startTime;
        this.timingMeasurements = timingMeasurements;
        this.tracingData = tracingData;
        this.timers = timers;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public String getRequestKey() {
        return this.requestKey;
    }

    public Map<String, Long> getTimingMeasurements() {
        return ImmutableMap.copyOf(this.timingMeasurements);
    }

    public Map<String, Long> getTimers() {
        return ImmutableMap.copyOf(this.timers);
    }

    public void addTimingMeasurement(String measurementKey, long measurementValue) {
        this.timingMeasurements.put(measurementKey, Long.valueOf(measurementValue));
    }

    public void startTimer(String timerKey) {
        this.timers.put(timerKey, Long.valueOf(DateTimeUtils.currentTimeMillis()));
    }

    public void stopTimer(String timerKey) {
        Long startTime = (Long) this.timers.get(timerKey);
        if (startTime != null) {
            this.addTimingMeasurement(timerKey, DateTimeUtils.currentTimeMillis() - startTime.longValue());
        }

    }

    public Map<String, String> getTracingData() {
        return ImmutableMap.copyOf(this.tracingData);
    }

    public void addTracingData(String key, String value) {
        this.tracingData.put(key, value);
    }

    public String toString(String fingerprint) {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("requestKey", this.requestKey);
        builder.append("startTime", this.startTime);
        if (fingerprint != null) {
            builder.append("fingerprint", fingerprint);
        }

        Iterator i$ = this.timingMeasurements.keySet().iterator();

        String key;
        while (i$.hasNext()) {
            key = (String) i$.next();
            builder.append(key, this.timingMeasurements.get(key));
        }

        i$ = this.tracingData.keySet().iterator();

        while (i$.hasNext()) {
            key = (String) i$.next();
            builder.append(key, '\"' + (String) this.tracingData.get(key) + '\"');
        }

        return builder.toString();
    }

    public String toString() {
        return this.toString((String) null);
    }
}
