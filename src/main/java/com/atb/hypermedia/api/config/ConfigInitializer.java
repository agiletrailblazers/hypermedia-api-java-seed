package com.atb.hypermedia.api.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

public class ConfigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(ConfigInitializer.class);

    private static final String CONFIG_BASE_PATH = "conf/";

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {

        ConfigurableEnvironment environment = ctx.getEnvironment();
        String environmentName = environment.getProperty("ATB_ENV", "test");
        logger.info("***************ATB_ENV is  {}***************", environmentName);

        MutablePropertySources propertySources = environment.getPropertySources();

        addExternalResource(environment, propertySources);

        addCommonAndCampEnvResources(environmentName, propertySources);
    }

    protected void addCommonAndCampEnvResources(String environmentName,
            MutablePropertySources propertySources) {

        try {
            propertySources.addLast(new ResourcePropertySource(CONFIG_BASE_PATH + environmentName + "/api.properties"));
            propertySources.addLast(new ResourcePropertySource(CONFIG_BASE_PATH + "common.api.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load required config", e);
        }
    }

    protected void addExternalResource(ConfigurableEnvironment environment,
            MutablePropertySources propertySources) {
        String overrideLocation= "file:"+environment.getProperty("externalConfigLocation", "");
        try {
            propertySources.addLast(new ResourcePropertySource(overrideLocation));
            logger.info("Loaded overrides from : {}", overrideLocation);
        } catch (IOException e) {
            logger.warn("Did not find external configuration file {}", overrideLocation);
        }
    }
}