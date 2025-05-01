package com.location.voitures.authentification;



import java.util.Objects;

/**
 * Secure data transfer object for user information
 * Shared between auth-service and other microservices
 */
public class UserInfoDTO {
    private final Long userId;
    private final String email;
    private final String role;
    private final String gov;

    public UserInfoDTO(Long long1, String email, String role,String gov ) {
        this.userId = long1;
        this.email = email;
        this.role = role;
        this.gov=gov;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
    
    
    public String getGov() {
        return gov;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoDTO that = (UserInfoDTO) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(email, that.email) && 
               Objects.equals(role, that.role)&&
        	   Objects.equals(gov, that.gov);
        
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, role,gov);
    }

    // toString()
    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", gov='" + gov + '\'' +
                
                '}';
    }
}