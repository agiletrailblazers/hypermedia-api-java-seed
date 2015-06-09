package com.atb.hypermedia.api.http.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.TrustStrategy;

public class AlwaysTrustingStrategy implements TrustStrategy {
    public AlwaysTrustingStrategy() {
    }

    public boolean isTrusted(X509Certificate[] certificate, String authType) throws CertificateException {
        return true;
    }
}
