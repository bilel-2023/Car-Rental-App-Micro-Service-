package com.location.voitures.agence.Config;

import com.location.voitures.agence.AuthServiceClient;
import com.location.voitures.agence.AuthServiceClient.AuthResponse;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.springframework.stereotype.Component;



public class JwtForwardFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtForwardFilter.class);
    private final AuthServiceClient authServiceClient;

    public JwtForwardFilter(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    public void init(FilterConfig filterConfig) {
        logger.info("JwtForwardFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        logger.debug("JwtForwardFilter processing request");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        // Log first 20 chars of token for debugging (avoid logging full token)
        logger.debug("Authorization header: {}",
                authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 20)) + "..." : "NULL");

        // 1. Check if Authorization header exists and is Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid Authorization header");
            return;
        }

        try {
            logger.debug("Forwarding token to Auth Service");
            AuthResponse authResponse = authServiceClient.validateToken(authHeader);

            // Log the response including role and user
            logger.info("Auth Service response - User ID: {}, Role: {}, Gov: {}",
                    authResponse.getUserId(), authResponse.getRole(), authResponse.getGov());

            // Validate the response
            if (authResponse == null || authResponse.getRole() == null || authResponse.getUserId() == null || authResponse.getGov() == null) {
                logger.error("Invalid response from Auth Service");
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid auth service response");
                return;
            }

            String role = authResponse.getRole();
            logger.debug("User role: {}", role);

            // Handle role-specific logic here
            if ("AGENCE".equals(role)) {
                // User is AGENCE, so proceed normally
                logger.debug("User has AGENCE role");
            } else if ("CLIENT".equals(role)) {
                // User is CLIENT, allow GET requests, but block POST/PUT/DELETE
                if (httpRequest.getMethod().equalsIgnoreCase("POST") || 
                    httpRequest.getMethod().equalsIgnoreCase("PUT") || 
                    httpRequest.getMethod().equalsIgnoreCase("DELETE")) {
                    logger.warn("Access denied for CLIENT on method: {}", httpRequest.getMethod());
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Requires AGENCE role for modification");
                    return;
                } else {
                    // Allow GET requests for CLIENT
                    logger.debug("User has CLIENT role - allowing GET requests");
                }
            } else {
                // If the role is neither AGENCE nor CLIENT, deny access
                logger.warn("Access denied for unknown role: {}", role);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Access denied");
                return;
            }

            // Add additional attributes to the request for downstream processing if necessary
            request.setAttribute("agencyId", authResponse.getUserId());
            request.setAttribute("userGov", authResponse.getGov());

            // Continue processing the request
            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Auth Service communication failed", e);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Authentication service error");
        }
    }

}