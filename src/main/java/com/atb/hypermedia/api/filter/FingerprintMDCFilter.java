package com.atb.hypermedia.api.filter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.joda.time.DateTimeUtils;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import com.google.common.base.Joiner;

/**
 * Puts a request fingerprint into the slf4j MDC under the key FINGERPRINT. The
 * fingerprint takes the form:
 * <HOSTNAME>-<PID>-<THREADNAME>-<SERVICE_CODE>-<SYSTEM_TIME_MS>
 */
public class FingerprintMDCFilter implements Filter {

    private final String APPLICATION_ID = "hypermedia-seed";

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequestWrapper httpRequestWrapper = new HttpServletRequestWrapper((HttpServletRequest) servletRequest);
        String fingerPrint;
        if(StringUtils.hasText(httpRequestWrapper.getHeader("FINGERPRINT"))) {
            fingerPrint = httpRequestWrapper.getHeader("FINGERPRINT");
        } else {
            fingerPrint = createFingerprint(hostName(), pid(), threadName());
        }

        MDC.put("FINGERPRINT", fingerPrint);
        HttpServletResponseWrapper httpResponseWrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        //TO DO: we should not get the FINGERPRINT in the response before this code. In case, it was added somewhere else
        //shall we log a warning here or just let it go?
        if (!httpResponseWrapper.containsHeader("FINGERPRINT")) httpResponseWrapper.addHeader("FINGERPRINT", fingerPrint);
        filterChain.doFilter(httpRequestWrapper,httpResponseWrapper);
        MDC.remove("FINGERPRINT");
    }

    protected String createFingerprint(String hostName, String pid, String threadId) {
        return Joiner.on("-").join(hostName, pid, threadId, APPLICATION_ID, currentTime());
    }

    protected String hostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    // Bit of a hack, but doesn't seem to be a good way to get a PID in Java...
    protected String pid() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    protected String threadName() {
        return Thread.currentThread().getName();
    }

    protected long currentTime() {
        return DateTimeUtils.currentTimeMillis();
    }

}
