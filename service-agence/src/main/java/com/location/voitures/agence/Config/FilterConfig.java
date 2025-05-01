package com.location.voitures.agence.Config;

import com.location.voitures.agence.AuthServiceClient;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
@Configuration
public class FilterConfig {

    private final AuthServiceClient authServiceClient;

    public FilterConfig(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Bean
    public JwtForwardFilter jwtForwardFilter() {
        return new JwtForwardFilter(authServiceClient);
    }
}