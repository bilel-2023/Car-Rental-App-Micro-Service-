package com.service_client.main.config;

import com.service_client.main.config.AuthServiceClient;
import com.service_client.main.config.AuthServiceClient.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtForwardFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(JwtForwardFilter.class);

    public JwtForwardFilter(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            logger.debug("Extracted token: {}", token);
            try {
                AuthResponse authResponse = authServiceClient.validateToken("Bearer " + token);
                logger.debug("AuthResponse: {}", authResponse);

                if (authResponse != null && authResponse.getUserId() != null && authResponse.getRole() != null) {
                    if (!"CLIENT".equalsIgnoreCase(authResponse.getRole())) {
                        logger.warn("Access denied: only CLIENT users are allowed");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Access denied: only CLIENT users are allowed");
                        return;
                    }

                    logger.debug("Setting clientId: {}", authResponse.getUserId());
                    request.setAttribute("clientId", authResponse.getUserId().longValue());

                } else {
                    logger.warn("Invalid or expired token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                logger.error("Error calling auth service: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error communicating with authentication service: " + e.getMessage());
                return;
            }
        } else {
            logger.warn("Missing token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            return;
        }

        filterChain.doFilter(request, response);
    }

}