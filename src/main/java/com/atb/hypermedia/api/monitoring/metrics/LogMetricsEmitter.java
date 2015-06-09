package com.atb.hypermedia.api.monitoring.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogMetricsEmitter implements MetricsEmitter {
    private static final Logger logger = LoggerFactory.getLogger(LogMetricsEmitter.class);

    static final char SET_START = '[';
    static final char SET_END = ']';
    static final char KEY_VALUE_DELIM = '=';
    static final char FIELD_DELIM = ',';

    public void emit(Map<String, Double> metrics) {
        logger.warn(generateLogMessage(metrics));
    }

    String generateLogMessage(Map<String, Double> metrics) {
        StringBuffer buff = new StringBuffer();
        buff.append("LogMetricsEmitter");
        buff.append(SET_START);
        if (metrics != null) {
            int x = 0;
            for (String metricName : metrics.keySet()) {

                buff.append(metricName);
                buff.append(KEY_VALUE_DELIM);
                buff.append(metrics.get(metricName));
                if (x < metrics.keySet().size() - 1) {
                    buff.append(FIELD_DELIM);
                }
                x++;
            }
        }
        buff.append(SET_END);
        return buff.toString();
    }
}
