package com.atb.hypermedia.api.monitoring;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * The MemoryStats class is used to expose the JVM Memory stats in a convenient MBean.
 */
@Component
@ManagedResource
public class MemoryStats {

    private MemoryMXBean memoryBean;

    /**
     * Constructor.
     */
    public MemoryStats() {
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * Returns the amount of memory in bytes that is committed for the Java virtual machine to use.
     * @return the committed memory usage in bytes.
     */
    @ManagedAttribute
    public long getCommitted() {
        return memoryBean.getHeapMemoryUsage().getCommitted();
    }

    /**
     * Returns the amount of used memory in bytes.
     * @return the used memory usage in bytes.
     */
    @ManagedAttribute
    public long getHeapUsed() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Returns the amount of memory in bytes that the Java virtual machine initially requests from
     * the operating system for memory management.
     * @return the init memory usage in bytes.
     */
    @ManagedAttribute
    public long getInitHeapSize() {
        return memoryBean.getHeapMemoryUsage().getInit();
    }

    /**
     * Returns the maximum amount of memory in bytes that can be used for memory management.
     * @return the max memory usage in bytes.
     */
    @ManagedAttribute
    public long getMaxHeapSize() {
        return memoryBean.getHeapMemoryUsage().getMax();
    }
}
