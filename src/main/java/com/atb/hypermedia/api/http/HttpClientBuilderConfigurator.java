package com.atb.hypermedia.api.http;

import java.io.IOException;
import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpClientBuilderConfigurator {
    void configure(HttpClientBuilder var1, HttpClientPropertySource var2, String var3) throws IOException;
}
