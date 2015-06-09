package com.atb.hypermedia.api.monitoring;


import java.util.Map;

/**
 * A MetricGenerator is a scheduled task that generates metric data.
 */
public interface MetricGenerator {

    /**
     * Generate metric data.
     */
    Map<String, Double> generateMetrics();
}
