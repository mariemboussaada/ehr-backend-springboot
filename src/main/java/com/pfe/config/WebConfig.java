package com.pfe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost",
                        "http://localhost:4200",
                        "http://10.0.2.2:4200",
                        "capacitor://localhost",
                        "http://10.0.2.2:1234",
                        "http://10.0.2.2",
                        "http://192.168.1.14:4200",
                        "http://192.168.1.14:1234",
                        "http://localhost:4200",     // Pour le web
                        "http://localhost:8100",     // Pour ionic serve
                        "http://10.0.2.2:4200",     // Pour l'émulateur Android
                        "capacitor://localhost"      // Pour l'app mobile
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}