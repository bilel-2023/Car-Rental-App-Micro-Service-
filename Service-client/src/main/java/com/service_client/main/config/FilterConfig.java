package com.service_client.main.config;

import com.service_client.main.config.AuthServiceClient;
import com.service_client.main.config.AuthServiceClient.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtForwardFilter> jwtForwardFilter(AuthServiceClient authServiceClient) {
        FilterRegistrationBean<JwtForwardFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtForwardFilter(authServiceClient));
        registration.addUrlPatterns("/locations/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("jwtForwardFilter");
        registration.setAsyncSupported(true);
        return registration;
    }
}