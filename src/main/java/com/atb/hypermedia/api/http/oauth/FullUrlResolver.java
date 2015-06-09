package com.atb.hypermedia.api.http.oauth;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

class FullUrlResolver {
    FullUrlResolver() {
    }

    public String getFullUrl(HttpRequest request, HttpContext context) {
        try {
            URI e = new URI(request.getRequestLine().getUri());
            if(e.isAbsolute()) {
                return e.toString();
            } else {
                HttpHost host = (HttpHost)context.getAttribute("http.target_host");
                return host + request.getRequestLine().getUri();
            }
        } catch (URISyntaxException var5) {
            throw new IllegalStateException("Request somehow acquired an invalid URI", var5);
        }
    }
}
