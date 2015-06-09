package com.atb.hypermedia.api.monitoring;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fishwife.jrugged.ServiceStatus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The StatusController responds to requests on /status by returning a page that
 * contains the health status for the application.  The HTTP Status code will be set to
 * 200 if the application health status is GREEN or YELLOW, and will be set to 503
 * if the application health status is RED.
 */
@Controller
public class StatusController implements ApplicationContextAware {

    @Inject
    private HealthStatusManager healthStatusManager;

    @Inject
    private StatusResponseInitializer statusResponseInitializer;

    private ApplicationContext context;

    @RequestMapping("/system/status")
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ServiceStatus currentStatus = healthStatusManager.getServiceStatus();

        statusResponseInitializer.initializeResponse(resp, currentStatus.getStatus());
        Map<String, Object> responseModel =
                statusResponseInitializer.buildStatusResponseModel(context.getEnvironment(), currentStatus);

        return new ModelAndView("status", responseModel);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
