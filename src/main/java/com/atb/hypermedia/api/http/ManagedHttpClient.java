package com.atb.hypermedia.api.http;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;

public class ManagedHttpClient implements HttpClient, Closeable, DisposableBean {
    private CloseableHttpClient httpClient;

    public ManagedHttpClient() {
    }

    public ManagedHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public final void destroy() throws IOException {
        this.close();
    }

    public void close() throws IOException {
        this.httpClient.close();
    }

    public HttpParams getParams() {
        return this.httpClient.getParams();
    }

    public ClientConnectionManager getConnectionManager() {
        return this.httpClient.getConnectionManager();
    }

    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException {
        return this.httpClient.execute(httpUriRequest);
    }

    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException {
        return this.httpClient.execute(httpUriRequest, httpContext);
    }

    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException {
        return this.httpClient.execute(httpHost, httpRequest);
    }

    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        return this.httpClient.execute(httpHost, httpRequest, httpContext);
    }

    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.httpClient.execute(httpUriRequest, responseHandler);
    }

    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException {
        return this.httpClient.execute(httpUriRequest, responseHandler, httpContext);
    }

    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.httpClient.execute(httpHost, httpRequest, responseHandler);
    }

    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException {
        return this.httpClient.execute(httpHost, httpRequest, responseHandler, httpContext);
    }
}
