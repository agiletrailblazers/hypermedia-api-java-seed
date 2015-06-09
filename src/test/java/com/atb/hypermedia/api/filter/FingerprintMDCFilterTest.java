package com.atb.hypermedia.api.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Joiner;

public class FingerprintMDCFilterTest {

    static String expectedFingerprint;

    @BeforeClass
    public static void setupClass() throws Exception {
        // Set the JODA time to a fixed time to make sure a slow test doesn't cause a test failure.
        DateTimeUtils.setCurrentMillisFixed(42L);

        String testThreadName = Thread.currentThread().getName();
        String testHostName = InetAddress.getLocalHost().getHostName();
        String testPid = ManagementFactory.getRuntimeMXBean().getName();
        expectedFingerprint = Joiner.on("-").join(testHostName, testPid, testThreadName, "hypermedia-seed", 42L);

    }

    @AfterClass
    public static void cleanupClass() {
        // Put the JODA time back to the system time for other tests.
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void whenFingerprintMDCStufferFilterIsRun_ItStuffsFingerprintIntoTheMDC() throws Exception {

        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request,
                    ServletResponse response)
                    throws IOException, ServletException {
                assertEquals(expectedFingerprint, MDC.get("FINGERPRINT"));
            }
        };

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        filter.doFilter(mockRequest, mockResponse, filterChain);
        assertNull(MDC.get("FINGERPRINT"));
    }

    @Test
    public void whenFingerprintMDCStufferFilterIsRun_ItAppendsFingerprintIntoTheMDC() throws Exception {

        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request,
                    ServletResponse response)
                    throws IOException, ServletException {
                assertEquals("test", MDC.get("FINGERPRINT"));
            }
        };

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("FINGERPRINT", "test");
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        filter.doFilter(mockRequest, mockResponse, filterChain);
        assertNull(MDC.get("FINGERPRINT"));
    }

    @Test
    public void whenFingerprintMDCStufferFilterIsRun_ItStuffsFingerprintIntoTheResponse() throws Exception {

        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request,
                    ServletResponse response)
                    throws IOException, ServletException {
                assertEquals(expectedFingerprint, MDC.get("FINGERPRINT"));
            }
        };

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        filter.doFilter(mockRequest, mockResponse, filterChain);
        assertEquals(expectedFingerprint, mockResponse.getHeader("FINGERPRINT"));
        assertNull(MDC.get("FINGERPRINT"));
    }

    @Test
    public void whenFingerprintMDCStufferFilterIsRun_ItAppendsFingerprintIntoTheResponse() throws Exception {

        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request,
                    ServletResponse response)
                    throws IOException, ServletException {
                assertEquals("test", MDC.get("FINGERPRINT"));
            }
        };

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("FINGERPRINT", "test");
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        filter.doFilter(mockRequest, mockResponse, filterChain);
        assertEquals("test", mockResponse.getHeader("FINGERPRINT"));
        assertNull(MDC.get("FINGERPRINT"));
    }

    @Test
    public void testInitDoesNothing() throws ServletException {
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        filter.init(null);
    }

    @Test
    public void testDestroyDoesNothing()  {
        FingerprintMDCFilter filter = new FingerprintMDCFilter();
        filter.destroy();
    }
}
