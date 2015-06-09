package com.atb.hypermedia.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;


public class HomeControllerTest {

    private HomeController controller;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        controller = new HomeController();
        response = new MockHttpServletResponse();
    }

    @Test
    public final void testContentXhtml() {
        assertEquals("home.html", controller.homeXhtml(response));
    }

    @Test
    public final void testContentHalJson() {
        assertEquals("home.hal.json", controller.homeHalJson(response));
    }

}