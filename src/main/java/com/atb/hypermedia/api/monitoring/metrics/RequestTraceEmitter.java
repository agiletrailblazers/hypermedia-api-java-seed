package com.atb.hypermedia.api.monitoring.metrics;

import com.atb.hypermedia.api.monitoring.trace.RequestTraceRecord;

public interface RequestTraceEmitter {
    void emit(RequestTraceRecord var1);
}
