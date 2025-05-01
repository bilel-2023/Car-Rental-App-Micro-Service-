package com.service_client.main.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "service-auth")
public interface AuthServiceClient {
    
    @GetMapping("/auth/validate-token")
    AuthResponse validateToken(@RequestHeader("Authorization") String token);

    class AuthResponse {
    	private String gov ;
        private Long userId;
        private String role;
        
        public Long getUserId() {
            if (userId == null) {
                throw new IllegalStateException("User ID cannot be null");
            }
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public String getGov() {
            return gov;
        }

        public void setGov(String gov) {
            this.gov = gov;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}