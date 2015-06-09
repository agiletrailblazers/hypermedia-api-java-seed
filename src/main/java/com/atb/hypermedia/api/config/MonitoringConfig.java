package com.atb.hypermedia.api.config;

import org.fishwife.jrugged.MonitoredService;
import org.fishwife.jrugged.RolledUpMonitoredService;
import org.fishwife.jrugged.aspects.CircuitBreakerAspect;
import org.fishwife.jrugged.aspects.PerformanceMonitorAspect;
import org.fishwife.jrugged.spring.CircuitBreakerBeanFactory;
import org.fishwife.jrugged.spring.PerformanceMonitorBeanFactory;
import org.fishwife.jrugged.spring.jmx.WebMBeanServerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.JmxUtils;

import javax.management.MBeanServer;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class MonitoringConfig {

    @Bean
    public RolledUpMonitoredService rolledUpStatusMonitor() {
        Set<MonitoredService> criticals = new HashSet<MonitoredService>();
        CircuitBreakerBeanFactory factory = circuitBreakerBeanFactory();

        Set<MonitoredService> nonCriticals = new HashSet<MonitoredService>();

        return getRolledUpMonitoredService("overallStatus", criticals, nonCriticals);
    }

    RolledUpMonitoredService getRolledUpMonitoredService(String name, Set<MonitoredService> criticals,
            Set<MonitoredService> nonCriticals) {
        return new RolledUpMonitoredService(name, criticals, nonCriticals);
    }

    @Bean
    public MBeanServer mBeanServer() {
        return JmxUtils.locateMBeanServer();
    }

    @Bean
    public MBeanExporter mBeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setServer(mBeanServer());

        return exporter;
    }

    @Bean
    public WebMBeanServerAdapter webMBeanServerAdapter() {
        return new WebMBeanServerAdapter(mBeanServer());
    }

    @Bean
    public CircuitBreakerBeanFactory circuitBreakerBeanFactory() {
        CircuitBreakerBeanFactory factory = getCircuitBreakerBeanFactory();
        factory.setPackageScanBase("com.campbell.manager");
        factory.setMBeanExportOperations(mBeanExporter());
        return factory;
    }

    CircuitBreakerBeanFactory getCircuitBreakerBeanFactory() {
        return new CircuitBreakerBeanFactory();
    }

    @Bean
    public CircuitBreakerAspect circuitBreakerAspect() {
        CircuitBreakerAspect aspect = new CircuitBreakerAspect();
        aspect.setCircuitBreakerFactory(circuitBreakerBeanFactory());
        return aspect;
    }

    @Bean
    public PerformanceMonitorBeanFactory performanceMonitorBeanFactory() {
        PerformanceMonitorBeanFactory factory = getPerformanceMonitorBeanFactory();
        factory.setPackageScanBase("com.campbell.manager");
        factory.setMBeanExportOperations(mBeanExporter());
        return factory;
    }

    PerformanceMonitorBeanFactory getPerformanceMonitorBeanFactory() {
        return new PerformanceMonitorBeanFactory();
    }

    @Bean
    public PerformanceMonitorAspect performanceMonitorAspect() {
        PerformanceMonitorAspect aspect = new PerformanceMonitorAspect();
        aspect.setPerformanceMonitorFactory(performanceMonitorBeanFactory());
        return aspect;
    }

}
