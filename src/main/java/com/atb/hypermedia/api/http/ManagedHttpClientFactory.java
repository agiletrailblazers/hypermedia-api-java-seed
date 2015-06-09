package com.atb.hypermedia.api.http;

import com.atb.hypermedia.api.http.oauth.OAuthHttpClientBuilderConfigurator;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.atb.hypermedia.api.http.util.AlwaysTrustingStrategy;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ManagedHttpClientFactory {
    public static final long DEFAULT_CONN_TTL = 300L;
    public static final int DEFAULT_CONN_POOL_SIZE = 50;
    public static final int DEFAULT_POOL_TIMEOUT = 100;
    public static final int DEFAULT_CONN_TIMEOUT = 100;
    public static final int DEFAULT_SOCKET_TIMEOUT = 500;
    private HttpClientPropertySource propertySource;
    private List<HttpClientBuilderConfigurator> configurators;

    public ManagedHttpClientFactory(HttpClientPropertySource propertySource, List<HttpClientBuilderConfigurator> configurators) {
        this.propertySource = propertySource;
        this.configurators = configurators;
    }

    public ManagedHttpClientFactory(HttpClientPropertySource propertySource) {
        this.propertySource = propertySource;
        this.configurators = Arrays.asList(new HttpClientBuilderConfigurator[]{new CachingHttpClientBuilderConfigurator(), new DecompressingHttpClientBuilderConfigurator(), new OAuthHttpClientBuilderConfigurator()});
    }

    public ManagedHttpClient create(String prefix, Iterable<HttpRequestInterceptor> requestInterceptors, Iterable<HttpResponseInterceptor> responseInterceptors) throws IOException {
        boolean caching = ((Boolean)this.propertySource.getProperty(prefix + ".httpclient.cache.enabled", Boolean.class, Boolean.valueOf(false))).booleanValue();
        Object builder = null;
        if(caching) {
            builder = CachingHttpClientBuilder.create();
        } else {
            builder = HttpClientBuilder.create();
        }

        if(requestInterceptors != null) {
            this.addRequestInterceptors(requestInterceptors, (HttpClientBuilder)builder);
        }

        if(responseInterceptors != null) {
            this.addResponseInterceptors(responseInterceptors, (HttpClientBuilder)builder);
        }

        this.configureRequestDefaults((HttpClientBuilder)builder, prefix);
        this.configureConnectionManager((HttpClientBuilder)builder, prefix);
        Iterator i$ = this.configurators.iterator();

        while(i$.hasNext()) {
            HttpClientBuilderConfigurator configurator = (HttpClientBuilderConfigurator)i$.next();
            configurator.configure((HttpClientBuilder)builder, this.propertySource, prefix);
        }

        return new ManagedHttpClient(((HttpClientBuilder)builder).build());
    }

    protected void addResponseInterceptors(Iterable<HttpResponseInterceptor> responseInterceptors, HttpClientBuilder builder) {
        Iterator i$ = responseInterceptors.iterator();

        while(i$.hasNext()) {
            HttpResponseInterceptor interceptor = (HttpResponseInterceptor)i$.next();
            builder.addInterceptorLast(interceptor);
        }

    }

    protected void addRequestInterceptors(Iterable<HttpRequestInterceptor> requestInterceptors, HttpClientBuilder builder) {
        Iterator i$ = requestInterceptors.iterator();

        while(i$.hasNext()) {
            HttpRequestInterceptor interceptor = (HttpRequestInterceptor)i$.next();
            builder.addInterceptorLast(interceptor);
        }

    }

    public ManagedHttpClient create(String prefix) throws IOException {
        return this.create(prefix, null, null);
    }

    private void configureSSLContextBuilder(SSLContextBuilder sslContextBuilder, String prefix) {
        boolean ignoreCerts = ((Boolean)this.propertySource.getProperty(prefix + ".httpclient.cert.ignore", Boolean.class, Boolean.FALSE)).booleanValue();

        try {
            if(ignoreCerts) {
                sslContextBuilder.loadTrustMaterial((KeyStore)null, new AlwaysTrustingStrategy());
            }

        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        } catch (KeyStoreException var6) {
            throw new RuntimeException(var6);
        }
    }

    private void configureConnectionManager(HttpClientBuilder builder, String prefix) {
        long connectionTTL = ((Long)this.propertySource.getProperty(prefix + ".httpclient.conn.ttl", Long.class, Long.valueOf(300L))).longValue();
        int poolSize = ((Integer)this.propertySource.getProperty(prefix + ".httpclient.conn.pool-size", Integer.class, Integer.valueOf(50))).intValue();

        try {
            SSLContextBuilder ex = new SSLContextBuilder();
            this.configureSSLContextBuilder(ex, prefix);

            Registry myReg = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory())
                                                     .register("https", new SSLConnectionSocketFactory(ex.build())).build();

            PoolingHttpClientConnectionManager connectionManager =
                    new PoolingHttpClientConnectionManager(
                            myReg,
                            (HttpConnectionFactory)null,
                            (SchemePortResolver)null,
                            (DnsResolver)null,
                            connectionTTL,
                            TimeUnit.SECONDS);
            connectionManager.setMaxTotal(poolSize);
            connectionManager.setDefaultMaxPerRoute(poolSize);
            builder.setConnectionManager(connectionManager);
        } catch (KeyManagementException var8) {
            throw new RuntimeException(var8);
        } catch (NoSuchAlgorithmException var9) {
            throw new RuntimeException(var9);
        }
    }

    private void configureRequestDefaults(HttpClientBuilder builder, String prefix) {
        int connTimeout = ((Integer)this.propertySource.getProperty(prefix + ".httpclient.conn-timeout-ms", Integer.class, Integer.valueOf(100))).intValue();
        int socketTimeout = ((Integer)this.propertySource.getProperty(prefix + ".httpclient.socket-timeout-ms", Integer.class, Integer.valueOf(500))).intValue();
        int poolTimeout = ((Integer)this.propertySource.getProperty(prefix + ".httpclient.pool-timeout-ms", Integer.class, Integer.valueOf(100))).intValue();
        Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectTimeout(connTimeout);
        requestConfigBuilder.setSocketTimeout(socketTimeout);
        requestConfigBuilder.setConnectionRequestTimeout(poolTimeout);
        builder.setDefaultRequestConfig(requestConfigBuilder.build());
    }
}
