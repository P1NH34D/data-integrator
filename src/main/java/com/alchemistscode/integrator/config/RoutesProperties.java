package com.alchemistscode.integrator.config;

import com.alchemistscode.integrator.config.wrapper.RoutesProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "config.file")
public class RoutesProperties {
    private Map<String, Map<String, String>> config = new HashMap<>();

    public Map<String, String> getPath(){
        config.computeIfAbsent("path", k -> new RoutesProperty<>());
        return config.get("path");
    }
    public Map<String, String> getRoutes(){
        config.computeIfAbsent("routes", k -> new RoutesProperty<>());
        return config.get("routes");
    }
}
