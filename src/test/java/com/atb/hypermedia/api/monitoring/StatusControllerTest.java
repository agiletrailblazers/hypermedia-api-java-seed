package com.atb.hypermedia.api.monitoring;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.fishwife.jrugged.ServiceStatus;
import org.fishwife.jrugged.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class StatusControllerTest {

    private StatusController statusController;

    @Mock
    HealthStatusManager mockHealthStatusManager;

    @Mock
    StatusResponseInitializer mockStatusResponseInitializer;

    @Mock
    Environment mockEnvironment;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @Before
    public void setUp() {
        statusController = new StatusController();
        ReflectionTestUtils.setField(statusController, "healthStatusManager", mockHealthStatusManager);
        ReflectionTestUtils.setField(statusController, "statusResponseInitializer", mockStatusResponseInitializer);

        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();

        ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
        statusController.setApplicationContext(mockApplicationContext);
        when(mockApplicationContext.getEnvironment()).thenReturn(mockEnvironment);
    }

    @Test
    public void testHandleRequest() throws Exception {
        ServiceStatus serviceStatus = new ServiceStatus("some_service", Status.UP, "some_reason");
        when(mockHealthStatusManager.getServiceStatus()).thenReturn(serviceStatus);
        Map<String, Object> responseModel = new HashMap<String, Object>();
        responseModel.put("test", new Object());
        when(mockStatusResponseInitializer.buildStatusResponseModel(mockEnvironment, serviceStatus))
                .thenReturn(responseModel);

        ModelAndView modelAndView = statusController.handleRequest(mockRequest, mockResponse);

        assertEquals("status", modelAndView.getViewName());
        verify(mockStatusResponseInitializer).initializeResponse(mockResponse, Status.UP);
        verify(mockStatusResponseInitializer).buildStatusResponseModel(mockEnvironment, serviceStatus);
        assertEquals(responseModel, modelAndView.getModel());
    }
}