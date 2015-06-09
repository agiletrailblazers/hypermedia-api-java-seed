package com.atb.hypermedia.api.monitoring;

import org.fishwife.jrugged.spring.PerformanceMonitorBean;
import org.fishwife.jrugged.spring.PerformanceMonitorBeanFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The PerfMonitorMetricGenerator generates metric data for all of the PerformanceMonitors.
 */
@Component
public class PerfMonitorMetricGenerator implements MetricGenerator {

    @Inject
    PerformanceMonitorBeanFactory perfMonitorBeanFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Double> generateMetrics() {

        Map<String, Double> metrics = new HashMap<String, Double>();

        Set<String> perfMonitorNames = perfMonitorBeanFactory.getPerformanceMonitorNames();

        for (String perfMonitorName: perfMonitorNames) {
            PerformanceMonitorBean performanceMonitor = perfMonitorBeanFactory.findPerformanceMonitorBean(perfMonitorName);
            addMetric(metrics, perfMonitorName, "success.count", performanceMonitor.getSuccessCount());
            addMetric(metrics, perfMonitorName, "success.latency-median",
                    performanceMonitor.getMedianPercentileSuccessLatencyLastMinute());
            addMetric(metrics, perfMonitorName, "success.latency-99th",
                    performanceMonitor.get99thPercentileSuccessLatencyLastMinute());
            addMetric(metrics, perfMonitorName, "failure.count", performanceMonitor.getFailureCount());
            addMetric(metrics, perfMonitorName, "failure.latency-median",
                    performanceMonitor.getMedianPercentileFailureLatencyLastMinute());
            addMetric(metrics, perfMonitorName, "failure.latency-99th",
                    performanceMonitor.get99thPercentileFailureLatencyLastMinute());
        }

        return metrics;
    }

    private void addMetric(
            Map<String, Double> metrics, String perfMonitorName, String metricName, double value) {
        metrics.put("perf-monitor." + perfMonitorName + "." + metricName, value);
    }
}
