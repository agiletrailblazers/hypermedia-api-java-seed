package com.atb.hypermedia.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

@RunWith(MockitoJUnitRunner.class)
public class ConfigControllerTest extends Mockito {

    private Properties applicationProperties;

    private ConfigController impl;

    @Mock
    private Model mockModel;

    @Before
    public void setUp() {
        applicationProperties = new Properties();

        impl = new ConfigController();
        ReflectionTestUtils.setField(impl, "applicationProperties", applicationProperties);
        ReflectionTestUtils.setField(impl, "propertyBlacklist", "");
        ReflectionTestUtils.setField(impl, "doNotList", new HashSet<String>());
    }

    @Test
    public void testListConfigReturnsConfigListView() {
        String view = impl.listConfig(mockModel);
        assertEquals("system/config.html", view);
    }

    @Test
    public void testListConfigPopulatesModel() {
        impl.listConfig(mockModel);

        verify(mockModel).addAttribute(eq("properties"), any(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListConfigPopulatesModelWithProperties() {
        final String key1 = "foo", val1 = "bar";
        final String key2 = "oof", val2 = "rab";

        applicationProperties.setProperty(key1, val1);
        applicationProperties.setProperty(key2, val2);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
        impl.listConfig(mockModel);
        verify(mockModel).addAttribute(eq("properties"), arg.capture());

        Map<String, String> propertyMap = arg.getValue();

        assertEquals(2, propertyMap.size());
        assertEquals(val1, propertyMap.get(key1));
        assertEquals(val2, propertyMap.get(key2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListConfigReturnsSortedMapOfProperties() {
        for (char c = 'z'; c >= 'a'; c--) {
            applicationProperties.setProperty("key" + c, "value" + c);
        }

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
        impl.listConfig(mockModel);
        verify(mockModel).addAttribute(eq("properties"), arg.capture());

        Map<String, String> propertyMap = arg.getValue();

        String lastKey = null;
        for (Entry<String, String> entry : propertyMap.entrySet()) {
            String currentKey = entry.getKey();
            if (lastKey != null) {
                assertTrue("Keys not ordered",
                        lastKey.compareTo(currentKey) < 0);
            }
            lastKey = currentKey;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitCreatesEmptyDoNotListSetFromNullBlacklist() {
        ReflectionTestUtils.setField(impl, "propertyBlacklist", null);

        impl.init();

        Set<String> doNotList = (Set<String>) ReflectionTestUtils.getField(
                impl, "doNotList");

        assertEquals(0, doNotList.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitCreatesEmptyDoNotListSetFromEmptyBlacklist() {
        ReflectionTestUtils.setField(impl, "propertyBlacklist", "");

        impl.init();

        Set<String> doNotList = (Set<String>) ReflectionTestUtils.getField(
                impl, "doNotList");

        assertEquals(0, doNotList.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitCreatesDoNotListSetFromTrimmedBlacklist() {
        final String prop1 = "prop1";
        final String prop2 = "prop2";
        final String prop3 = "prop3";
        ReflectionTestUtils.setField(impl, "propertyBlacklist", prop1 + ", "
                + prop2 + ",prop3");

        impl.init();

        Set<String> doNotList = (Set<String>) ReflectionTestUtils.getField(
                impl, "doNotList");

        assertEquals(3, doNotList.size());
        assertTrue(doNotList.contains(prop1));
        assertTrue(doNotList.contains(prop2));
        assertTrue(doNotList.contains(prop3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBlacklistedPropertiesOmmitted() {
        final String key1 = "foo", val1 = "bar";
        final String key2 = "oof", val2 = "rab";
        final String badKey1 = "bad";
        final String badKey2 = "beef";

        applicationProperties.setProperty(key1, val1);
        applicationProperties.setProperty(key2, val2);
        applicationProperties.setProperty(badKey1, "da");
        applicationProperties.setProperty(badKey2, "bomb");

        Set<String> badKeys = new HashSet<String>();
        badKeys.add(badKey1);
        badKeys.add(badKey2);

        ReflectionTestUtils.setField(impl, "doNotList", badKeys);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
        impl.listConfig(mockModel);
        verify(mockModel).addAttribute(eq("properties"), arg.capture());

        Map<String, String> propertyMap = arg.getValue();

        assertEquals(2, propertyMap.size());
        assertEquals(val1, propertyMap.get(key1));
        assertEquals(val2, propertyMap.get(key2));
        assertTrue(!propertyMap.containsKey(badKey1));
        assertTrue(!propertyMap.containsKey(badKey2));
    }
}