package com.atb.hypermedia.api.http.oauth;

import com.atb.hypermedia.api.http.HttpClientBuilderConfigurator;
import com.atb.hypermedia.api.http.HttpClientPropertySource;
import org.apache.http.impl.client.HttpClientBuilder;

public class OAuthHttpClientBuilderConfigurator implements HttpClientBuilderConfigurator {
    public OAuthHttpClientBuilderConfigurator() {
    }

    private String validatingGetKey(HttpClientPropertySource propertySource, String prefix, String keyName) {
        String fullKeyName = prefix + "." + keyName;
        String key = (String)propertySource.getProperty(fullKeyName, String.class, (String)null);
        if(key == null) {
            throw new IllegalArgumentException(fullKeyName + " missing from properties.");
        } else {
            return key;
        }
    }

    public void configure(HttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) {
        boolean isOauthEnabled = ((Boolean)propertySource.getProperty(prefix + ".httpclient.oauth.enabled", Boolean.class, Boolean.valueOf(false))).booleanValue();
        boolean isIauthEnabled = ((Boolean)propertySource.getProperty(prefix + ".httpclient.iauth.enabled", Boolean.class, Boolean.valueOf(false))).booleanValue();
        if(isOauthEnabled) {
            String oauthKey = this.validatingGetKey(propertySource, prefix, "httpclient.oauth.key");
            String oauthSecret = this.validatingGetKey(propertySource, prefix, "httpclient.oauth.secret");
            httpClientBuilder.addInterceptorLast(new OAuthingHttpRequestInterceptor(oauthKey, oauthSecret, "HMAC-SHA1"));
        }

    }
}
