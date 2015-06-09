package com.atb.hypermedia.api.monitoring;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.fishwife.jrugged.ServiceStatus;
import org.fishwife.jrugged.Status;
import org.springframework.core.env.Environment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.springframework.stereotype.Component;

/**
 * The StatusResponseInitializer initializes the {@link HttpServletResponse} headers and response model for a
 * status request.
 */
@Component
public class StatusResponseInitializer {

    @SuppressWarnings("deprecation")
    private final Map<Status, Integer> responseCodeMap =
            new ImmutableMap.Builder<Status, Integer>()
                    .put(Status.FAILED, HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .put(Status.INIT, HttpServletResponse.SC_SERVICE_UNAVAILABLE)
                    .put(Status.DOWN, HttpServletResponse.SC_SERVICE_UNAVAILABLE)
                    .put(Status.DEGRADED, HttpServletResponse.SC_OK)
                    .put(Status.BYPASS, HttpServletResponse.SC_OK)
                    .put(Status.UP, HttpServletResponse.SC_OK).build();

    /**
     * Initialize the {@link HttpServletResponse} headers and response model.
     * @param response the HttpServletResponse to initialize.
     * @param status the {@link Status} value.
     * @throws Exception
     */
    public void initializeResponse(HttpServletResponse response, Status status) throws Exception {
        setResponseCode(status, response);
        setAppropriateWarningHeaders(response, status);
        setCachingHeaders(response);
    }

    /**
     * Build the response model for a specified {@link Environment} and {@link ServiceStatus}.
     * @param environment the {@link Environment}.
     * @param serviceStatus the {@link ServiceStatus}.
     * @return the response model.
     */
    public Map<String, Object> buildStatusResponseModel(Environment environment, ServiceStatus serviceStatus) {
        Map<String, Object> responseModel = Maps.newHashMap();

        // build props
        responseModel.put("ahpBuildLife", environment.getProperty("build.life.id", "Unknown"));
        responseModel.put("gitHash", environment.getProperty("build.git-hash", "Unknown"));
        responseModel.put("compiledOn", environment.getProperty("build.compiled-on", "Unkown"));

        // environment info
        responseModel.put("atbEnv", environment.getProperty("ATB_ENV", "NOT SET"));
        responseModel.put("status", serviceStatus.getStatus().toString());
        responseModel.put("reason", StringUtils.join(serviceStatus.getReasons(), ","));

        try {
            responseModel.put("host", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
           responseModel.put("host", "unknown");
        }

        return responseModel;
    }

    private void setCachingHeaders(HttpServletResponse resp) {
        long now = System.currentTimeMillis();
        resp.setDateHeader("Date", now);
        resp.setDateHeader("Expires", now);
        resp.setHeader("Cache-Control", "no-cache");
    }

    private void setAppropriateWarningHeaders(HttpServletResponse resp, Status currentStatus) {
        if (Status.DEGRADED.equals(currentStatus)) {
            resp.addHeader("Warning", "199 jrugged \"Status degraded\"");
        }
    }

    private void setResponseCode(Status currentStatus, HttpServletResponse resp) {
        if (responseCodeMap.containsKey(currentStatus)) {
            resp.setStatus(responseCodeMap.get(currentStatus));
        }
    }
}