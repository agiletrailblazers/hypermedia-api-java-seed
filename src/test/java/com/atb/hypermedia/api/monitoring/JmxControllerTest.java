package com.atb.hypermedia.api.monitoring;

import org.fishwife.jrugged.spring.jmx.WebMBeanAdapter;
import org.fishwife.jrugged.spring.jmx.WebMBeanServerAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JmxControllerTest {

    JmxController jmxController;

    @Mock
    WebMBeanServerAdapter mockWebAdapter;

    MockHttpServletRequest req;
    MockHttpServletResponse resp;

    private final String ENCODING = "UTF-8";

    @Before
    public void setUp() {
        jmxController = new JmxController();
        ReflectionTestUtils.setField(jmxController, "webMBeanServerAdapter", mockWebAdapter);

        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();
    }

    @Test
    public void testRoot() {
        Set<String> mBeanNames = new HashSet<String>();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("mbeans", mBeanNames);
        when(mockWebAdapter.getMBeanNames()).thenReturn(mBeanNames);

        ModelAndView modelAndView = jmxController.root();

        Assert.assertEquals("jmx/root", modelAndView.getViewName());
        Assert.assertEquals(model, modelAndView.getModel());
    }

    @Test
    public void testShowBean() throws Exception {
        String objectName = "some_object_name";
        WebMBeanAdapter mockMBeanAdapter = mock(WebMBeanAdapter.class);

        Map<String, MBeanAttributeInfo> attributeMetadata =
                new HashMap<String, MBeanAttributeInfo>();
        Map<String, MBeanOperationInfo> operationMetadata =
                new HashMap<String, MBeanOperationInfo>();
        Map<String, Object> attributeValueMap = new HashMap<String, Object>();

        when(mockWebAdapter.createWebMBeanAdapter(objectName, ENCODING)).thenReturn(mockMBeanAdapter);
        when(mockMBeanAdapter.getAttributeMetadata()).thenReturn(attributeMetadata);
        when(mockMBeanAdapter.getOperationMetadata()).thenReturn(operationMetadata);
        when(mockMBeanAdapter.getAttributeValues()).thenReturn(attributeValueMap);

        ModelAndView modelAndView = jmxController.showBean(objectName);

        Assert.assertEquals("jmx/bean", modelAndView.getViewName());
        Assert.assertEquals(4, modelAndView.getModel().size());
        Assert.assertEquals(objectName, modelAndView.getModel().get("objectName"));
        Assert.assertEquals(attributeMetadata, modelAndView.getModel().get("attributeMetadata"));
        Assert.assertEquals(operationMetadata, modelAndView.getModel().get("operationMetadata"));
        Assert.assertEquals(attributeValueMap, modelAndView.getModel().get("attributeValueMap"));
    }

    @Test
    public void testShowAttribute() throws Exception {
        String objectName = "some_object_name";
        WebMBeanAdapter mockMBeanAdapter = mock(WebMBeanAdapter.class);

        String value = "some_value";
        String attributeName = "some_attribute_name";

        when(mockWebAdapter.createWebMBeanAdapter(objectName, ENCODING)).thenReturn(mockMBeanAdapter);
        when(mockMBeanAdapter.getAttributeValue(attributeName)).thenReturn(value);

        ModelAndView modelAndView = jmxController.showAttribute(objectName, attributeName);

        Assert.assertEquals("jmx/value", modelAndView.getViewName());
        Assert.assertEquals(1, modelAndView.getModel().size());
        Assert.assertEquals(value, modelAndView.getModel().get("value"));
    }

    @Test
    public void testShowOperation() throws Exception {
        String objectName = "some_object_name";
        WebMBeanAdapter mockMBeanAdapter = mock(WebMBeanAdapter.class);

        MBeanOperationInfo mockOperationInfo = mock(MBeanOperationInfo.class);
        String operationName = "some_operation_name";

        when(mockWebAdapter.createWebMBeanAdapter(objectName, ENCODING)).thenReturn(mockMBeanAdapter);
        when(mockMBeanAdapter.getOperationInfo(operationName)).thenReturn(mockOperationInfo);

        ModelAndView modelAndView = jmxController.showOperation(objectName, operationName);

        Assert.assertEquals("jmx/operation", modelAndView.getViewName());
        Assert.assertEquals(3, modelAndView.getModel().size());
        Assert.assertEquals(objectName, modelAndView.getModel().get("objectName"));
        Assert.assertEquals(operationName, modelAndView.getModel().get("operationName"));
        Assert.assertEquals(mockOperationInfo, modelAndView.getModel().get("operationInfo"));
    }

    @Test
    public void testInvokeOperation() throws Exception {
        String objectName = "some_object_name";
        WebMBeanAdapter mockMBeanAdapter = mock(WebMBeanAdapter.class);

        String value = "some_value";
        String operationName = "some_operation_name";
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        when(mockWebAdapter.createWebMBeanAdapter(objectName, ENCODING)).thenReturn(mockMBeanAdapter);
        when(mockMBeanAdapter.invokeOperation(operationName, parameterMap)).thenReturn(value);

        ModelAndView modelAndView = jmxController.invokeOperation(req, objectName, operationName);

        Assert.assertEquals("jmx/value", modelAndView.getViewName());
        Assert.assertEquals(1, modelAndView.getModel().size());
        Assert.assertEquals(value, modelAndView.getModel().get("value"));
    }

}
