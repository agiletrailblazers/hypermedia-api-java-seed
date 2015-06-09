package com.atb.hypermedia.api.monitoring.metrics;

import com.atb.hypermedia.api.monitoring.GraphiteMonitor;

import java.util.Map;

public class GraphiteMetricsEmitter implements MetricsEmitter {

    private GraphiteMonitor graphiteMonitor;

    public GraphiteMetricsEmitter(GraphiteMonitor monitor) {
        this.graphiteMonitor = monitor;
    }

    public void emit(Map<String, Double> metrics) {
        if (metrics != null) {
            for (String metricName : metrics.keySet()) {
                graphiteMonitor.sendData(metricName, metrics.get(metricName));
            }
        }
    }
}
