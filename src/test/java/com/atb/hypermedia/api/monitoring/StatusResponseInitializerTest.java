package com.atb.hypermedia.api.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.fishwife.jrugged.ServiceStatus;
import org.fishwife.jrugged.Status;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class StatusResponseInitializerTest {

    private StatusResponseInitializer statusResponseInitializer;
    private MockHttpServletResponse response;

    @Mock
    Environment mockEnvironment;

    @Before
    public void setUp() {
        statusResponseInitializer = new StatusResponseInitializer();
        response = new MockHttpServletResponse();
    }

    private void assertResponseCodeIs(Status status, int code) throws Exception {
        statusResponseInitializer.initializeResponse(response, status);
        assertEquals(code, response.getStatus());
    }

    private void assertValueForStatusIs(Status status, String reason) throws Exception {
        ServiceStatus serviceStatus = new ServiceStatus("some_service", status, reason);
        Map<String,Object> responseModel = statusResponseInitializer.buildStatusResponseModel(mockEnvironment, serviceStatus);
        assertEquals(status.toString(), responseModel.get("status"));
        assertEquals(reason, responseModel.get("reason"));
    }

    @Test
    public void returns200IfStatusIsUp() throws Exception {
        assertResponseCodeIs(Status.UP, 200);
    }

    @Test
    public void returns503IfStatusIsDown() throws Exception {
        assertResponseCodeIs(Status.DOWN, 503);
    }

    @Test
    public void returns200IfStatusIsDegraded() throws Exception {
        assertResponseCodeIs(Status.DEGRADED, 200);
    }

    @Test
    public void setsWarningHeaderIfDegraded() throws Exception {
        statusResponseInitializer.initializeResponse(response, Status.DEGRADED);
        boolean found = false;
        for (Object val : response.getHeaders("Warning")) {
            if ("199 jrugged \"Status degraded\"".equals(val)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void returns200IfStatusIsBypass() throws Exception {
        assertResponseCodeIs(Status.BYPASS, 200);
    }

    @Test
    public void returns500IfStatusIsFailed() throws Exception {
        assertResponseCodeIs(Status.FAILED, 500);
    }

    @Test
    public void returns503IfStatusIsInit() throws Exception {
        assertResponseCodeIs(Status.INIT, 503);
    }

    @Test
    public void writesStatusOutInResponseBodyWhenUp() throws Exception {
        assertValueForStatusIs(Status.UP, "some_reason");
    }

    @Test
    public void writesStatusOutInResponseBodyWhenDown() throws Exception {
        assertValueForStatusIs(Status.DOWN, "some_reason");
    }

    @Test
    public void setsNonCacheableHeaders() throws Exception {
        statusResponseInitializer.initializeResponse(response, Status.UP);
        assertNotNull(response.getHeader("Expires"));
        assertEquals(response.getHeader("Date"), response.getHeader("Expires"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
    }

    @Test
    @Ignore
    public void setsAHPBuildLife() throws Exception {
        Environment env = mock(Environment.class);
        when(env.getProperty("build.life.id", "Unknown")).thenReturn("huh");
        ServiceStatus serviceStatus = new ServiceStatus("some_service", Status.UP, "");
        Map<String, Object> responseModel =
                statusResponseInitializer.buildStatusResponseModel(mockEnvironment, serviceStatus);
        assertEquals("huh", responseModel.get("ahpBuildLife"));
    }

    @Test
    @Ignore
    public void setsATBENV() throws Exception {
        Environment env = mock(Environment.class);
        when(env.getProperty("ATB_ENV", "NOT SET")).thenReturn("what");
        ServiceStatus serviceStatus = new ServiceStatus("some_service", Status.UP, "");
        Map<String, Object> responseModel =
                statusResponseInitializer.buildStatusResponseModel(mockEnvironment, serviceStatus);
        assertEquals("what", responseModel.get("atbEnv"));
    }
}
