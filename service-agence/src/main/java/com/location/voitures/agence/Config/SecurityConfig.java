package com.location.voitures.agence.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtForwardFilter jwtForwardFilter;

    public SecurityConfig(JwtForwardFilter jwtForwardFilter) {
        this.jwtForwardFilter = jwtForwardFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cars/**").permitAll() // All cars endpoints are accessible after our filter checks
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtForwardFilter, UsernamePasswordAuthenticationFilter.class); // Add your filter before Spring Security auth

        return http.build();
    }
}
