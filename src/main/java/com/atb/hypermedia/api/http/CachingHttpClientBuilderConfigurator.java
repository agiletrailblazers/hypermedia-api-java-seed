package com.atb.hypermedia.api.http;

import java.io.IOException;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.client.cache.CacheConfig.Builder;
import org.apache.http.impl.client.cache.memcached.MemcachedHttpCacheStorage;

public class CachingHttpClientBuilderConfigurator implements HttpClientBuilderConfigurator {
    public CachingHttpClientBuilderConfigurator() {
    }

    public void configure(HttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) throws IOException {
        if(((Boolean)propertySource.getProperty(prefix + ".httpclient.cache.enabled", Boolean.class, Boolean.valueOf(false))).booleanValue()) {
            if(!(httpClientBuilder instanceof CachingHttpClientBuilder)) {
                throw new IllegalArgumentException("Impossible to configure caching with a builder of type " + httpClientBuilder.getClass().getName());
            }

            CachingHttpClientBuilder cachingHttpClientBuilder = (CachingHttpClientBuilder)httpClientBuilder;
            this.configureBackend(cachingHttpClientBuilder, propertySource, prefix);
        }

    }

    protected void configureBackend(CachingHttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) throws IOException {
        String backendProperty = prefix + ".httpclient.cache.backend";
        String backend = (String)propertySource.getProperty(backendProperty, String.class, null);
        if(backend == null) {
            throw new IllegalArgumentException(backendProperty + " missing from properties.");
        } else {
            if(backend.equals("memory")) {
                this.configureMemory(httpClientBuilder, propertySource, prefix);
            } else {
                if(!backend.equals("memcached")) {
                    throw new IllegalArgumentException("Unknown value " + backend + " for " + backendProperty + " in properties.");
                }

                this.configureMemcache(httpClientBuilder, propertySource, prefix);
            }

        }
    }

    protected void configureMemory(CachingHttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) throws IOException {
        int maxCacheEntries = ((Integer)propertySource.getProperty(prefix + ".httpclient.cache.memory.max-cache-entries", Integer.class, Integer.valueOf(100))).intValue();
        int maxObjectSize = ((Integer)propertySource.getProperty(prefix + ".httpclient.cache.memory.max-object-size", Integer.class, Integer.valueOf(100000))).intValue();
        boolean sharedCache = ((Boolean)propertySource.getProperty(prefix + ".httpclient.cache.memory.shared-cache", Boolean.class, Boolean.valueOf(false))).booleanValue();
        Builder cacheConfigBuilder = CacheConfig.custom().setMaxCacheEntries(maxCacheEntries).setMaxObjectSize((long)maxObjectSize).setSharedCache(sharedCache);
        httpClientBuilder.setCacheConfig(cacheConfigBuilder.build());
    }

    protected void configureMemcache(CachingHttpClientBuilder httpClientBuilder, HttpClientPropertySource propertySource, String prefix) throws IOException {
        Builder cacheConfigBuilder = CacheConfig.custom();
        cacheConfigBuilder.setSharedCache(((Boolean)propertySource.getProperty(prefix + ".httpclient.cache.memcached.shared-cache", Boolean.class, Boolean.valueOf(false))).booleanValue());
        cacheConfigBuilder.setHeuristicCachingEnabled(((Boolean)propertySource.getProperty(prefix + ".httpclient.cache.memcached.heuristic-caching-enabled", Boolean.class, Boolean.valueOf(true))).booleanValue());
        cacheConfigBuilder.setHeuristicDefaultLifetime(((Long)propertySource.getProperty(prefix + ".httpclient.cache.memcached.heuristic-default-lifetime", Long.class, Long.valueOf(900L))).longValue());
        cacheConfigBuilder.setMaxCacheEntries(((Integer)propertySource.getProperty(prefix + ".httpclient.cache.memcached.max-cache-entries", Integer.class, Integer.valueOf(100))).intValue());
        cacheConfigBuilder.setMaxObjectSize((long)((Integer)propertySource.getProperty(prefix + ".httpclient.cache.memcached.max-object-size-bytes", Integer.class, Integer.valueOf(100000))).intValue());
        httpClientBuilder.setCacheConfig(cacheConfigBuilder.build());
        httpClientBuilder.setHttpCacheStorage(this.createMemcachedStorage(propertySource, prefix));
    }

    protected HttpCacheStorage createMemcachedStorage(HttpClientPropertySource propertySource, String prefix) throws IOException {
        return this.newMemcachedHttpCacheStorage(this.createMemcachedClient(propertySource, prefix));
    }

    protected MemcachedClientIF createMemcachedClient(HttpClientPropertySource propertySource, String prefix) throws IOException {
        ConnectionFactoryBuilder factory = new ConnectionFactoryBuilder();
        factory.setProtocol((Protocol)propertySource.getProperty(prefix + ".httpclient.cache.memcached.protocol", Protocol.class, Protocol.BINARY));
        factory.setOpTimeout(((Long)propertySource.getProperty(prefix + ".httpclient.cache.memcached.operation-timeout", Long.class, Long.valueOf(1000L))).longValue());
        factory.setTimeoutExceptionThreshold(((Integer)propertySource.getProperty(prefix + ".httpclient.cache.memcached.timeout-exception-threshold", Integer.class, Integer.valueOf(300))).intValue());
        factory.setLocatorType((Locator)propertySource.getProperty(prefix + ".httpclient.cache.memcached.locator-type", Locator.class, Locator.CONSISTENT));
        factory.setFailureMode((FailureMode)propertySource.getProperty(prefix + ".httpclient.cache.memcached.failure-mode", FailureMode.class, FailureMode.Redistribute));
        factory.setHashAlg((HashAlgorithm)propertySource.getProperty(prefix + ".httpclient.cache.memcached.hash-algoirthm", HashAlgorithm.class, DefaultHashAlgorithm.KETAMA_HASH));
        return new MemcachedClient(factory.build(), AddrUtil.getAddresses((String)propertySource.getProperty(prefix + ".httpclient.cache.memcached.host-list", String.class, null)));
    }

    protected MemcachedHttpCacheStorage newMemcachedHttpCacheStorage(MemcachedClientIF memcachedClient) {
        return new MemcachedHttpCacheStorage(memcachedClient);
    }
}
