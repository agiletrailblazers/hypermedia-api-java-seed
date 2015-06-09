package com.atb.hypermedia.api.controller;

import com.atb.hypermedia.api.config.OutputMediaType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles requests for pieces of content.
 */
@Controller
public class HomeController {
    /**
     * Display the home page.
     *
     * @return the string name of the view
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_XHTML_XML_VALUE)
    public String homeXhtml(HttpServletResponse response) {
        return "home.html";
    }

    /**
     * Display the home page as HAL+JSON.
     *
     * @return the string name of the view
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = OutputMediaType.APPLICATION_HAL_JSON_VALUE)
    public String homeHalJson(HttpServletResponse response) {
        return "home.hal.json";
    }

}