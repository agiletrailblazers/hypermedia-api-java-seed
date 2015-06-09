package com.atb.hypermedia.api.http.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

class OAuthContext {
    private final OAuthAccessor accessor;
    private final OAuthMessage message;

    public OAuthContext(OAuthAccessor accessor, OAuthMessage message) {
        this.accessor = accessor;
        this.message = message;
    }

    public String generateAuthorizationHeader(String realm) throws IOException, OAuthException, URISyntaxException {
        this.message.addRequiredParameters(this.accessor);
        return this.message.getAuthorizationHeader(realm);
    }
}