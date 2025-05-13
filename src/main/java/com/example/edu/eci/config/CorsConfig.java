package com.example.edu.eci.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS) in
 * the application.
 *
 * This class defines a bean that creates a CORS filter to handle requests from
 * different origins.
 * It allows requests from any origin, supports common HTTP methods, and permits
 * any headers.
 * However, it does not allow credentials to be included in the requests.
 *
 * The configuration is applied globally to all endpoints in the application.
 */
@Configuration
public class CorsConfig {

    /**
     * This method creates a CorsFilter bean that allows requests from any origin
     * ("*"),
     * supports HTTP methods such as GET, POST, PUT, DELETE, and OPTIONS, and
     * permits
     * any headers in the request. It does not allow credentials to be included in
     * the requests.
     *
     * The configuration is applied to all endpoints ("/**").
     *
     * @return a CorsFilter instance configured with the specified CORS settings.
     */
    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}