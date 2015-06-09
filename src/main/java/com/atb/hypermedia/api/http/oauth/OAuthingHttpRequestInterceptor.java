package com.atb.hypermedia.api.http.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import net.oauth.OAuthException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.ParseException;
import org.apache.http.protocol.HttpContext;

class OAuthingHttpRequestInterceptor implements HttpRequestInterceptor {
    private final OAuthContextFactory oauthContextFactory;
    private final PostParamsExtractor postParamsExtractor;
    private final FullUrlResolver fullUrlResolver;

    public OAuthingHttpRequestInterceptor(String consumerKey, String consumerSecret, String signatureMethod) {
        this.oauthContextFactory = new OAuthContextFactory(consumerKey, consumerSecret, signatureMethod);
        this.postParamsExtractor = new PostParamsExtractor();
        this.fullUrlResolver = new FullUrlResolver();
    }

    OAuthingHttpRequestInterceptor(OAuthContextFactory oauthContextFactory, PostParamsExtractor postParamsExtractor, FullUrlResolver fullUrlResolver) {
        this.oauthContextFactory = oauthContextFactory;
        this.postParamsExtractor = postParamsExtractor;
        this.fullUrlResolver = fullUrlResolver;
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        try {
            this.signRequest(request, context);
        } catch (OAuthException var4) {
            throw new HttpException("Failed to attach OAuth headers", var4);
        } catch (URISyntaxException var5) {
            throw new HttpException("Failed to attach OAuth headers", var5);
        }
    }

    private void signRequest(HttpRequest request, HttpContext context) throws ParseException, IOException, OAuthException, URISyntaxException {
        String requestMethod = request.getRequestLine().getMethod().toUpperCase();
        Collection params = null;
        if("POST".equals(requestMethod) && request instanceof HttpEntityEnclosingRequest) {
            params = this.postParamsExtractor.extractPostParamsIfNecessary((HttpEntityEnclosingRequest)request);
        }

        String fullUrl = this.fullUrlResolver.getFullUrl(request, context);
        OAuthContext oauthContext = this.oauthContextFactory.getInstance(requestMethod, fullUrl, params);
        request.addHeader("Authorization", oauthContext.generateAuthorizationHeader((String)null));
    }
}
