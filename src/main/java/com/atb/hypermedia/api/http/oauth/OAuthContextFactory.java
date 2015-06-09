package com.atb.hypermedia.api.http.oauth;

import java.util.Collection;
import java.util.Map.Entry;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;

class OAuthContextFactory {
    private final String consumerKey;
    private final String consumerSecret;
    private final String signatureMethod;

    public OAuthContextFactory(String consumerKey, String consumerSecret, String signatureMethod) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.signatureMethod = signatureMethod;
    }

    public OAuthContext getInstance(String method, String url, Collection<? extends Entry<String, String>> params) {
        OAuthConsumer consumer = new OAuthConsumer("", this.consumerKey, this.consumerSecret, (OAuthServiceProvider)null);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        OAuthMessage message = new OAuthMessage(method, url, params);
        message.addParameter("oauth_signature_method", this.signatureMethod);
        return new OAuthContext(accessor, message);
    }
}
