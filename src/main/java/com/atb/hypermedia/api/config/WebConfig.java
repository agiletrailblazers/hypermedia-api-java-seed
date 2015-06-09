package com.atb.hypermedia.api.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 *  Web Configuration
 */
//@ComponentScan(basePackages = "com.campbell.microdata.serialization")
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    private static final String CONFIG_BASE_PATH = "conf/";

    @Inject
    private Environment env;

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/views/");
        return freeMarkerConfigurer;
    }

    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setSuffix(".ftl");
        return viewResolver;
    }

    @Bean
    public static Properties applicationProperties() throws IOException {
        PropertiesFactoryBean ppc = new PropertiesFactoryBean();
        ppc.setLocations(new Resource[] {
                new ClassPathResource("manager-build.properties"),
                new ClassPathResource(CONFIG_BASE_PATH + "common.api.properties"),
                new ClassPathResource(CONFIG_BASE_PATH +  System.getProperty("ATB_ENV", "test") + "/api.properties")
                                });
        ppc.setIgnoreResourceNotFound(true);
        ppc.afterPropertiesSet();
        return ppc.getObject();
    }

    /**
     * This is required and used by Spring MVC Value annotations to inject properties.
     * @return PropertySourcesPlaceholderConfigurer
     * @throws IOException
     */
    @Bean
    static public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setProperties(applicationProperties());
        return pspc;
    }

    //////////////////////////////
    // HAL Serialization Wiring //
    //////////////////////////////

    @Bean
    public JsonNodeFactory jsonNodeFactory() {
        return JsonNodeFactory.instance;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true); //TODO Make configurable?
        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handler = super.requestMappingHandlerMapping();
        handler.setUseSuffixPatternMatch(false);
        handler.setAlwaysUseFullPath(true);
        return handler;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //Adding a custom converter wipes out the defaults. Couldn't find an easier
        //way to add back the defaults
        converters.add(mappingJacksonHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
    }
}