package com.atb.hypermedia.api.http.oauth;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.oauth.OAuth;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ContentType;

class PostParamsExtractor {
    private final ContentTypeResolver contentTypeResolver;

    public PostParamsExtractor() {
        this.contentTypeResolver = new ContentTypeResolver();
    }

    PostParamsExtractor(ContentTypeResolver contentTypeResolver) {
        this.contentTypeResolver = contentTypeResolver;
    }

    public Collection<? extends Entry<String, String>> extractPostParamsIfNecessary(HttpEntityEnclosingRequest post) throws ParseException, IOException {
        List params = null;
        ContentType contentType = this.contentTypeResolver.getContentType(post);
        if(contentType != null && this.isUrlEncoded(contentType)) {
            HttpEntity entity = post.getEntity();
            if(entity != null) {
                String body = EntityUtils.toString(entity);
                params = OAuth.decodeForm(body);
                post.setEntity(new StringEntity(body, contentType));
            }
        }

        return params;
    }

    private boolean isUrlEncoded(ContentType contentType) {
        String mimeType = contentType.getMimeType();
        return mimeType != null?"application/x-www-form-urlencoded".equals(mimeType.toLowerCase()):false;
    }
}
