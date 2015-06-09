package com.atb.hypermedia.api.http;

import com.atb.hypermedia.api.http.ManagedHttpClient;
import com.atb.hypermedia.api.http.ManagedHttpClientFactory;
import com.atb.hypermedia.api.http.SpringEnvironmentHttpClientPropertySource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

/**
 * The HttpClientFactory is used to build HttpClient implementations.
 */
@Component
public class HttpClientFactory {

    @Inject
    private Environment env;

    /**
     * Build a managedHttpClient for a specified service prefix.
     * @param prefix the service prefix.
     * @return the created managedHttpClient.
     */
    public ManagedHttpClient create(String prefix) {
        SpringEnvironmentHttpClientPropertySource propertySource =
                new SpringEnvironmentHttpClientPropertySource(env);
        ManagedHttpClientFactory clientFactory = new ManagedHttpClientFactory(propertySource);
        try {
            return clientFactory.create(prefix);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
