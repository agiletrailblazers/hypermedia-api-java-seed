package com.atb.hypermedia.api.monitoring.metrics;

import com.google.common.collect.ImmutableSortedMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class LogMetricsEmitterTest {

    private LogMetricsEmitter emitter;

    @Before
    public void setUp(){
        emitter = new LogMetricsEmitter();
    }


    @Test
    public void test() {
        assertEquals("LogMetricsEmitter[boss=2.0,what=3.0,yo=1.0]",
                emitter.generateLogMessage(ImmutableSortedMap.of("yo", 1.0, "boss", 2.0, "what", 3.0)));
    }

    @Test
    public void testNull() {
        assertEquals("LogMetricsEmitter[]", emitter.generateLogMessage(null));
    }

    @Test
    public void testEmpty() {
        assertEquals("LogMetricsEmitter[]", emitter.generateLogMessage(new HashMap<String,Double>()));
    }
}
