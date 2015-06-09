package com.atb.hypermedia.api.monitoring;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.fishwife.jrugged.RolledUpMonitoredService;
import org.fishwife.jrugged.ServiceStatus;
import org.fishwife.jrugged.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
* The HealthStatusManager is used to keep track of the application health
* status and also to allow the health status to be overridden.
*/
@ManagedResource
@Component
public class HealthStatusManager {

    private static final Logger logger = LoggerFactory.getLogger(HealthStatusManager.class);

    @Resource(name="rolledUpStatusMonitor")
    private RolledUpMonitoredService healthStatus;

    private ServiceStatus healthStatusOverride = null;

    private static final String OVERRIDE_REASON = "Override";

    /**
     * Get the application health service status.
     * If there is a health status override in place then the override value will be returned.
     * @return the application Health ServiceStatus.
     */
    public ServiceStatus getServiceStatus() {
        if (healthStatusOverride != null) {
            return healthStatusOverride;
        }
        return healthStatus.getServiceStatus();
    }

    /**
     * Get the application health status as a human-readable string.
     * If there is a health status override in place then the override value will be returned.
     * @return the String representation of the application health status.
     */
    @ManagedAttribute
    public String getApplicationStatus() {
        return getServiceStatus().getStatus().getSignal();
    }

    /**
     * Get the reason for the application health status.
     * @return the comma-separated list of reasons for the application health status.
     */
    @ManagedAttribute
    public String getStatusReason() {
        return StringUtils.join(healthStatus.getServiceStatus().getReasons(), ",");
    }

    /**
     * Get the natural health status.
     *   This is the status the application would be in if there were no overrides in effect.
     * @return the natural HealthStatus.
     */
    @ManagedAttribute
    public Status getNaturalStatus() {
        return healthStatus.getServiceStatus().getStatus();
    }

    /**
     * Get the application natural health status as a human-readable string.
     * This is the status the application would be in if there were no overrides in effect.
     * @return the String representation of the application natural health status.
     */
    @ManagedAttribute
    public String getApplicationNaturalStatus() {
        return healthStatus.getServiceStatus().getStatus().getSignal();
    }

    /**
     * Check if there is a health status override.
     * @return true if there is an override, false if not.
     */
    @ManagedAttribute
    public boolean getStatusOverride() {
        return (healthStatusOverride != null);
    }

    /**
     * Set the health status override to GREEN.
     */
    @ManagedOperation
    public void overrideStatusToGreen() {
        logger.info("Health Status Override to " + Status.UP);
        this.healthStatusOverride =
                new ServiceStatus(healthStatus.getServiceStatus().getName(), Status.UP, OVERRIDE_REASON);
    }

    /**
     * Set the health status override to YELLOW.
     */
    @ManagedOperation
    public void overrideStatusToYellow() {
        logger.info("Health Status Override to " + Status.DEGRADED);
        this.healthStatusOverride =
                new ServiceStatus(healthStatus.getServiceStatus().getName(), Status.DEGRADED, OVERRIDE_REASON);
    }

    /**
     * Set the health status override to RED.
     */
    @ManagedOperation
    public void overrideStatusToRed() {
        logger.info("Health Status Override to " + Status.DOWN);
        this.healthStatusOverride =
                new ServiceStatus(healthStatus.getServiceStatus().getName(), Status.DOWN, OVERRIDE_REASON);
    }

    /**
     * Clear the health status override
     */
    @ManagedOperation
    public void clearStatusOverride() {
        logger.info("Health Status Override cleared, Health Status is now " + healthStatus);
        this.healthStatusOverride = null;
    }

    public List<ServiceStatus> getCriticalServiceStatuses() {
        return healthStatus.getCriticalStatuses();
    }

    public List<ServiceStatus> getNonCriticalServiceStatuses() {
        return healthStatus.getNonCriticalStatuses();
    }
}
