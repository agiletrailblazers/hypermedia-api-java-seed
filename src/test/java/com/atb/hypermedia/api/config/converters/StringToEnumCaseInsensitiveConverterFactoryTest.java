package com.atb.hypermedia.api.config.converters;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StringToEnumCaseInsensitiveConverterFactoryTest {

    StringToEnumCaseInsensitiveConverterFactory undertest;

    @Before
    public void setup() throws Exception {
        undertest = new StringToEnumCaseInsensitiveConverterFactory();
    }

    @Test
    public void testGetConverter() {
        Converter<String, TimeUnit> converter = undertest.getConverter(TimeUnit.class);

        assertNotNull(converter);
        assertEquals(TimeUnit.DAYS, converter.convert("dAyS"));
        assertEquals(TimeUnit.HOURS, converter.convert("hours"));
        assertEquals(TimeUnit.SECONDS, converter.convert("SECONDS"));
    }

}
