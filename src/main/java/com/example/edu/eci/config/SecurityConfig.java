package com.example.edu.eci.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

/**
 * Security configuration class for the application.
 * This class configures Spring Security to manage authentication and
 * authorization.
 * It includes beans for authentication management, password encoding, and
 * security filter chains.

 * Annotations:
 * - @Configuration: Marks this class as a configuration class for Spring.
 * - @EnableWebSecurity: Enables Spring Security's web security support.
 * - @EnableMethodSecurity: Enables method-level security annotations such
 * as @Secured and @RolesAllowed.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    /**
     * AuthenticationManager: Provides the authentication manager for the
     * application, configured using the AuthenticationConfiguration.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines a BCryptPasswordEncoder bean for encoding passwords securely.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain to handle HTTP request
     * authorization, login, and logout functionality.
     * <p>
     * Security Configuration:
     * - Permits access to "/auth/login" and "/auth/logout" endpoints without
     * authentication.
     * - Front does the authentication and sends the token in the header.
     * - Disables CSRF protection.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
