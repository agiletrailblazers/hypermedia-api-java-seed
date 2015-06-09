package com.atb.hypermedia.api.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for configuration information.
 */
@Controller
@RequestMapping("/system/config")
public class ConfigController {

    private static final String CONFIG_LIST_VIEW = "system/config.html";

    @Autowired
    private Properties applicationProperties;

    @Value("${display.blacklist:}")
    private String propertyBlacklist;

    private Set<String> doNotList;

    @PostConstruct
    public void init() {
        doNotList = new HashSet<String>();

        if (propertyBlacklist == null) {
            return;
        }

        for (String propertyKey : propertyBlacklist.split(",")) {
            String trimmedKey = propertyKey.trim();
            if (!"".equals(trimmedKey)) {
                doNotList.add(propertyKey.trim());
            }
        }
    }

    /**
     * Lists the configured properties in html.
     *
     * @param model model to populate
     * @return view to use for display
     */
    @RequestMapping(method = RequestMethod.GET)
    public String listConfig(Model model) {
        Map<String, String> propertyMap  = new TreeMap<String, String>();
        for (Entry<Object, Object> property : applicationProperties.entrySet()) {
            String key = property.getKey().toString();
            if (!doNotList.contains(key)) {
                String value = property.getValue().toString();
                propertyMap.put(key, value);
            }
        }
        model.addAttribute("properties", propertyMap);
        return CONFIG_LIST_VIEW;
    }
}
