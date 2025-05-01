package com.location.voitures.authentification;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


import org.springframework.stereotype.Component;

@Component  
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/auth/register") || path.startsWith("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // Return 403 if trying to access protected resource without token
            return;
        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);

            userRepository.findByEmail(email).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });

            filterChain.doFilter(request, response);  // Continue normally
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // Token invalid, forbid
        }
    }

}