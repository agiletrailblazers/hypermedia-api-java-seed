package com.atb.hypermedia.api.monitoring;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.servlet.http.HttpServletRequest;

import org.fishwife.jrugged.spring.jmx.WebMBeanAdapter;
import org.fishwife.jrugged.spring.jmx.WebMBeanServerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;

/**
 * The JmxController presents the JMX beans in the application in a human-readable
 * web interface.
 */
@Controller
@RequestMapping("/system/jmx")
public class JmxController {

    @Inject
    private WebMBeanServerAdapter webMBeanServerAdapter;

    private final String ENCODING = "UTF-8";

    /**
     * Get the list of MBeans.
     * @return the ModelAndView with the list of MBeans that are available.
     */
    @RequestMapping("/")
    public ModelAndView root() {
        Set<String> mBeanNames = webMBeanServerAdapter.getMBeanNames();
        return new ModelAndView("jmx/root", "mbeans", mBeanNames);
    }

    /**
     * Get a summary of the attributes, operations, and attribute values for an MBean.
     * @param objectName the name of the MBean.
     * @return the ModelAnvView with the attribute metadata, operation metadata, and attribute values.
     */
    @RequestMapping("/bean")
    public ModelAndView showBean(
            @RequestParam(value = "objectName", required = true) String objectName)
            throws JMException, UnsupportedEncodingException {

        WebMBeanAdapter webMBeanAdapter = webMBeanServerAdapter.createWebMBeanAdapter(objectName, ENCODING);
        Map<String, MBeanAttributeInfo> attributeMetadata = webMBeanAdapter.getAttributeMetadata();
        Map<String, MBeanOperationInfo> operationsMetadata = webMBeanAdapter.getOperationMetadata();
        Map<String, Object> attributeValueMap = webMBeanAdapter.getAttributeValues();

        return new ModelAndView("jmx/bean",
                ImmutableMap.of("objectName", objectName,
                        "attributeMetadata", attributeMetadata,
                        "operationMetadata", operationsMetadata,
                        "attributeValueMap", attributeValueMap));
    }

    /**
     * Get the value for an MBean Attribute.
     * @param objectName the name of the MBean.
     * @param attributeName the name of the attribute.
     * @return the ModelAndView with the value.
     */
    @RequestMapping(value = "/value", method = {RequestMethod.GET})
    public ModelAndView showAttribute(
            @RequestParam(value = "objectName", required = true) String objectName,
            @RequestParam(value = "attributeName", required = true) String attributeName)
            throws JMException, UnsupportedEncodingException {

        WebMBeanAdapter webMBeanAdapter = webMBeanServerAdapter.createWebMBeanAdapter(objectName, ENCODING);
        Object value = webMBeanAdapter.getAttributeValue(attributeName);
        return new ModelAndView("jmx/value", "value", value);
    }

    /**
     * Get the metadata for an operation.
     * @param objectName the name of the MBean.
     * @param operationName the name of the operation.
     * @return the ModelAndView with the object name, operation name, and operation metadata.
     */
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    public ModelAndView showOperation(
            @RequestParam(value = "objectName", required = true) String objectName,
            @RequestParam(value = "operationName", required = true) String operationName)
            throws JMException, UnsupportedEncodingException {

        WebMBeanAdapter webMBeanAdapter = webMBeanServerAdapter.createWebMBeanAdapter(objectName, ENCODING);
        MBeanOperationInfo operationInfo = webMBeanAdapter.getOperationInfo(operationName);
        return new ModelAndView("jmx/operation",
                ImmutableMap.of("objectName", objectName, "operationName", operationName, "operationInfo", operationInfo));
    }

    /**
     * Invoke a method on an MBean.
     * @param req the {@link HttpServletRequest}.
     * @param objectName the name of the MBean.
     * @param operationName the name of the operation.
     * @return the ModelAndView with the returned value from the invocation.
     */
    @RequestMapping(value = "/invoke", method = {RequestMethod.POST})
    public ModelAndView invokeOperation(HttpServletRequest req,
            @RequestParam(value = "objectName", required = true) String objectName,
            @RequestParam(value = "operationName", required = true) String operationName)
            throws JMException, UnsupportedEncodingException {

        WebMBeanAdapter webMBeanAdapter = webMBeanServerAdapter.createWebMBeanAdapter(objectName, ENCODING);

        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = req.getParameterMap();

        Object value = webMBeanAdapter.invokeOperation(operationName, parameterMap);
        return new ModelAndView("jmx/value", "value", value);
    }
}
