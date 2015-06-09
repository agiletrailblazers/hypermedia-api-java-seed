package com.atb.hypermedia.api.monitoring;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LazyHostResolver {
    private String localHostName = null;
    private InetAddress graphiteHostInetAddress = null;

    public LazyHostResolver() {
    }

    public String resolveLocalHost() throws UnknownHostException {
        if(this.localHostName == null) {
            this.localHostName = this.getLocalHost().getCanonicalHostName();
        }

        return this.localHostName;
    }

    public InetAddress resolveHost(String hostname) throws UnknownHostException {
        if(this.graphiteHostInetAddress == null) {
            this.graphiteHostInetAddress = this.getHost(hostname);
        }

        return this.graphiteHostInetAddress;
    }

    InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    InetAddress getHost(String hostname) throws UnknownHostException {
        return InetAddress.getByName(hostname);
    }
}
