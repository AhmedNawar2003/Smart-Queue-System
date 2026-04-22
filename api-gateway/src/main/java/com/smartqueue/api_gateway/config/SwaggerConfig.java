package com.smartqueue.api_gateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    // بيجمع الـ Swagger docs بتاعة كل الـ services في صفحة واحدة
    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();

        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        urls.add(createUrl("auth-service",
                "/auth-docs/v3/api-docs"));
        urls.add(createUrl("user-service",
                "/user-docs/v3/api-docs"));
        urls.add(createUrl("queue-service",
                "/queue-docs/v3/api-docs"));
        urls.add(createUrl("appointment-service",
                "/appointment-docs/v3/api-docs"));
        urls.add(createUrl("notification-service",
                "/notification-docs/v3/api-docs"));
        urls.add(createUrl("analytics-service",
                "/analytics-docs/v3/api-docs"));

        properties.setUrls(urls);
        return properties;
    }

    private AbstractSwaggerUiConfigProperties.SwaggerUrl createUrl(
            String name, String url) {
        var swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
        swaggerUrl.setName(name);
        swaggerUrl.setUrl(url);
        return swaggerUrl;
    }
}