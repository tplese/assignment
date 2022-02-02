package com.webshop.assignment.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rate-api")
public class ApplicationConfiguration {

    private String eurUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
