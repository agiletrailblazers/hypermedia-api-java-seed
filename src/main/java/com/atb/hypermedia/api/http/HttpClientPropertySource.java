package com.atb.hypermedia.api.http;

public interface HttpClientPropertySource {
    <T> T getProperty(String var1, Class<T> var2, T var3);
}
