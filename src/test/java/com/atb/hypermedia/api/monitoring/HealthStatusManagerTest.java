package com.atb.hypermedia.api.monitoring;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.fishwife.jrugged.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import org.fishwife.jrugged.RolledUpMonitoredService;
import org.fishwife.jrugged.ServiceStatus;

public class HealthStatusManagerTest extends Mockito {

    private HealthStatusManager healthStatusManager;

    @Mock
    RolledUpMonitoredService mockRolledUpStatus;

    private final String SERVICE_NAME = "some_service";

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        healthStatusManager = new HealthStatusManager();
        ReflectionTestUtils.setField(healthStatusManager, "healthStatus", mockRolledUpStatus);
    }

    @Test
    public void testUpHealthStatus() {
        // Test UP with no override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        healthStatusManager.clearStatusOverride();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.UP);
    }

    @Test
    public void testDegradedHealthStatus() {
        // Test DEGRADED with no override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.DEGRADED));
        healthStatusManager.clearStatusOverride();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.DEGRADED);
    }

    @Test
    public void testDownHealthStatus() {
        // Test DOWN with no override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.DOWN));
        healthStatusManager.clearStatusOverride();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.DOWN);
    }

    @Test
    public void testGetStatusReason_withReasons() {
        String reason1 = "some_reason";
        String reason2 = "another_reason";
        List<String> reasons = new ArrayList<String>();
        reasons.add(reason1);
        reasons.add(reason2);
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(new ServiceStatus("Test", Status.DOWN, reasons));

        String reason = healthStatusManager.getStatusReason();

        assertEquals(reason1 + "," + reason2, reason);
    }

    @Test
    public void testGetStatusReason_withNoReasons() {
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(new ServiceStatus("Test", Status.UP));
        String reason = healthStatusManager.getStatusReason();
        assertEquals("", reason);
    }

    @Test
    public void testApplicationHealthStatus() {
        // Test stringified status.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        assertEquals(healthStatusManager.getApplicationStatus(), Status.UP.getSignal());

    }
    @Test
    public void testOverrideUpHealthStatus() {
        // Test DOWN with UP override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.DOWN));
        healthStatusManager.overrideStatusToGreen();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.UP, "Override");
    }

    @Test
    public void testOverrideDegradedHealthStatus() {
        // Test DOWN with DEGRADED override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.DOWN));
        healthStatusManager.overrideStatusToYellow();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.DEGRADED, "Override");
    }

    @Test
    public void testOverrideDownHealthStatus() {
        // Test UP with DOWN override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        healthStatusManager.overrideStatusToRed();
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.DOWN, "Override");
    }

    @Test
    public void testClearOverrideHealthStatus() {
        // Test clear override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        healthStatusManager.overrideStatusToRed();
        assertEquals(healthStatusManager.getStatusOverride(), true);
        healthStatusManager.clearStatusOverride();
        assertEquals(healthStatusManager.getStatusOverride(), false);
        verifyServiceStatus(healthStatusManager.getServiceStatus(), Status.UP);
    }

    @Test
    public void testNaturalHealthStatus() throws Exception {
        // Test natural status with override.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        healthStatusManager.overrideStatusToRed();
        assertEquals(healthStatusManager.getNaturalStatus(), Status.UP);
    }

    @Test
    public void testNaturalApplicationHealthStatus() throws Exception {
        // Test stringified natural status.
        when(mockRolledUpStatus.getServiceStatus()).thenReturn(createServiceStatus(Status.UP));
        healthStatusManager.overrideStatusToRed();
        assertEquals(healthStatusManager.getApplicationNaturalStatus(), Status.UP.getSignal());
    }

    @Test
    public void testGetCriticalServiceStatuses() {
        List<ServiceStatus> criticalList = new ArrayList<ServiceStatus>();
        ServiceStatus serviceStatus = createServiceStatus(Status.UP);
        criticalList.add(serviceStatus);
        when(mockRolledUpStatus.getCriticalStatuses()).thenReturn(criticalList);

        List<ServiceStatus> retrievedList = healthStatusManager.getCriticalServiceStatuses();
        assertTrue(retrievedList.contains(serviceStatus));
    }

    @Test
    public void testGetNonCriticalServiceStatuses() {
        List<ServiceStatus> nonCriticalList = new ArrayList<ServiceStatus>();
        ServiceStatus serviceStatus = createServiceStatus(Status.UP);
        nonCriticalList.add(serviceStatus);
        when(mockRolledUpStatus.getNonCriticalStatuses()).thenReturn(nonCriticalList);

        List<ServiceStatus> retrievedList = healthStatusManager.getNonCriticalServiceStatuses();
        assertTrue(retrievedList.contains(serviceStatus));
    }

    private ServiceStatus createServiceStatus(Status status) {
        return new ServiceStatus(SERVICE_NAME, status);
    }


    private void verifyServiceStatus(ServiceStatus serviceStatus, Status status) {
        assertEquals(SERVICE_NAME, serviceStatus.getName());
        assertEquals(status, serviceStatus.getStatus());
        List<String> reasons = serviceStatus.getReasons();
        assertEquals(0, reasons.size());
    }

    private void verifyServiceStatus(ServiceStatus serviceStatus,Status status, String reason) {
        assertEquals(SERVICE_NAME, serviceStatus.getName());
        assertEquals(status, serviceStatus.getStatus());
        List<String> reasons = serviceStatus.getReasons();
        assertEquals(1, reasons.size());
        assertTrue(reasons.contains(reason));
    }
}
