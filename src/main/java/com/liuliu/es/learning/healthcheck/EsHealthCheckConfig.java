package com.liuliu.es.learning.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsHealthCheckConfig {

    @Value("${spring.application.name}") 
    private String applicationName;
    
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> healthCheckWebServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/" + applicationName);
    }
}
