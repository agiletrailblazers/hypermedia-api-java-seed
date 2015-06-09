package com.atb.hypermedia.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages={"com.campbell.manager.config"})
@EnableAspectJAutoProxy(proxyTargetClass=true) //This allows jrugged to do its thing, the proxyTargetClass = true allows us to properly proxy classes and not need interfaces.
public class ComponentConfig {
}
