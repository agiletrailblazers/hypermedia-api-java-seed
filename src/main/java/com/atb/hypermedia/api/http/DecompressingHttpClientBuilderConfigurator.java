package com.atb.hypermedia.api.http;

import org.apache.http.impl.client.HttpClientBuilder;

public class DecompressingHttpClientBuilderConfigurator implements HttpClientBuilderConfigurator {
    public DecompressingHttpClientBuilderConfigurator() {
    }

    public void configure(HttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) {
        if(!((Boolean)propertySource.getProperty(prefix + ".httpclient.decompressing.enabled", Boolean.class, Boolean.valueOf(false))).booleanValue()) {
            httpClientBuilder.disableContentCompression();
        }

    }
}
