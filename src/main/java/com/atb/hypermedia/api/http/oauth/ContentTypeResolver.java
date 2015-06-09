package com.atb.hypermedia.api.http.oauth;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;

class ContentTypeResolver {
    ContentTypeResolver() {
    }

    public ContentType getContentType(HttpEntityEnclosingRequest request) {
        HttpEntity entity = request.getEntity();
        return entity != null?ContentType.get(entity):this.deriveContentTypeFromHeaders(request);
    }

    private ContentType deriveContentTypeFromHeaders(HttpEntityEnclosingRequest request) {
        Header contentTypeHeader = request.getFirstHeader("Content-Type");
        if(contentTypeHeader != null && contentTypeHeader.getValue() != null) {
            try {
                return ContentType.parse(contentTypeHeader.getValue());
            } catch (ParseException var4) {
                ;
            }
        }

        return null;
    }
}
