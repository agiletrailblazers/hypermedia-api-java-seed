package com.atb.hypermedia.api.http;

import org.springframework.core.env.Environment;

public class SpringEnvironmentHttpClientPropertySource implements HttpClientPropertySource {
    private final Environment env;

    public SpringEnvironmentHttpClientPropertySource(Environment env) {
        this.env = env;
    }

    public <T> T getProperty(String s, Class<T> tClass, T t) {
        return this.env.getProperty(s, tClass, t);
    }
}
