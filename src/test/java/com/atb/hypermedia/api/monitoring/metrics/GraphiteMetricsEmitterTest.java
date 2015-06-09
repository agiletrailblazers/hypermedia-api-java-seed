package com.atb.hypermedia.api.monitoring.metrics;

import com.atb.hypermedia.api.monitoring.GraphiteMonitor;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GraphiteMetricsEmitterTest {


    private GraphiteMonitor graphiteMonitor;

    private GraphiteMetricsEmitter emitter;

    @Before
    public void setUp() {
        graphiteMonitor = mock(GraphiteMonitor.class);
        emitter = new GraphiteMetricsEmitter(graphiteMonitor);
    }

    @Test
    public void test() {
        Map<String, Double> data = ImmutableMap.of("yo", 1.0, "boss", 2.0);
        emitter.emit(data);
        verify(graphiteMonitor, times(1)).sendData("yo", 1.0);
        verify(graphiteMonitor, times(1)).sendData("boss", 2.0);
    }


    @Test
    public void testNull() {
        Map<String, Double> data = ImmutableMap.of("yo", 1.0, "boss", 2.0);
        emitter.emit(null);
        verifyZeroInteractions(graphiteMonitor);
    }


    @Test
    public void testEmpty() {
        Map<String, Double> data = new HashMap<String, Double>();
        emitter.emit(data);
        verifyZeroInteractions(graphiteMonitor);
    }
}
