package com.example.notification_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications") // optional, but good for clarity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String customerEmail;
    private String message;
    private LocalDateTime sentAt;

    public Notification() {
    }

    public Notification(String id, String customerEmail, String message, LocalDateTime sentAt, boolean sentSuccessfully) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.message = message;
        this.sentAt = sentAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    
}
