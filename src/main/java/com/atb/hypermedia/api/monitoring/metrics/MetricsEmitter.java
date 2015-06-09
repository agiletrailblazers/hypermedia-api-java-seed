package com.atb.hypermedia.api.monitoring.metrics;

import java.util.Map;

public interface MetricsEmitter {

     void emit(Map<String, Double> metrics);
}
