package com.atb.hypermedia.api.monitoring;

import com.atb.hypermedia.api.monitoring.metrics.MetricsEmitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * The GraphiteMetricScheduler is used to scheduler tasks to send metric data to various emitters.
 */
@Component
public class MetricsEmissionScheduler {

    @Inject
    private List<MetricsEmitter> emitters;

    @Inject
    private PerfMonitorMetricGenerator perfMonitorMetricGenerator;

    @Value("${monitoring.emission.enabled}")
    private boolean enabled;

    /**
     * This method runs the MetricGenerators every minute.
     */
    @Scheduled(fixedRate=60000)
    public void everyMinute() {
        if (enabled) {
            sendMetrics(perfMonitorMetricGenerator.generateMetrics());
        }
    }

    private void sendMetrics(Map<String, Double> metrics) {
        for (MetricsEmitter emitter : emitters){
            emitter.emit(metrics);
        }
    }
}
