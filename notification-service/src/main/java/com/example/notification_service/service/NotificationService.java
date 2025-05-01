package com.example.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.location.voitures.authentification.MailWelcome;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = "Client-Welcome", groupId = "notification-group")
    public void handleWelcomeEvent(MailWelcome message) {
        try {
            String email = message.getUserEmail();
            String name = message.getUserName(); // Retrieve the username

            sendWelcomeEmail(email, name); // Pass the name to the email sending method
        } catch (Exception e) {
            // Handle failure (logging, etc.)
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(String email, String name) { // Updated method signature to accept name
        String subject = "Welcome to Carya!";
        String content = String.format("Bonjour %s,\n\n", name) + // Personalized greeting
                         "Bienvenue sur Carya! Nous sommes ravis de vous avoir parmi nous.\n" +
                         "Nous espérons que vous apprécierez votre expérience avec nous.\n\n" +
                         "Cordialement,\nL'équipe Carya";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}