package com.example.ueberholserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.api-key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Delegate CORS to the existing CorsConfig (WebMvcConfigurer)
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            // Allow same-origin iframes so the H2 console UI renders correctly
            .headers(headers -> headers
                    .frameOptions(fo -> fo.sameOrigin()))
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                    new ApiKeyAuthFilter(apiKey),
                    UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/**").authenticated()
                    // H2 console and actuator remain open
                    .anyRequest().permitAll());

        return http.build();
    }
}
