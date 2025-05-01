package com.location.voitures.authentification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.location.voitures.exceptions.authexceptions.*;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    private final KafkaTemplate<String, MailWelcome> kafkaTemplate; // Inject KafkaTemplate

    
    @Value("${kafka.order.topic}")
    private String topic;
    
    public AuthController(UserRepository repo, 
                        PasswordEncoder encoder,
                        JwtUtil jwtUtil,
                        KafkaTemplate<String, MailWelcome> kafkaTemplate){
        this.userRepository = repo;
        this.passwordEncoder = encoder;
        this.jwtUtil = jwtUtil;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    
    
    @GetMapping("/validate-token")
    public UserInfoDTO validateToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");  // Ensure you remove the "Bearer " part before extracting username


        String email = jwtUtil.extractUsername(jwtToken);
        
        // Log email after extracting from JWT
        System.out.println("Extracted email: " + email);
        

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("User Role: " + user.getRole());  

        return new UserInfoDTO(
            user.getId(),
            user.getEmail(),
            user.getRole(),
            user.getgovernorate()
        );
    }

    
    
    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> editProfile(@RequestHeader("Authorization") String token,
                                                    @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        String jwtToken = token.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update fields if provided in the request
        if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getAge() != null && updateRequest.getAge() >= 21) {
            user.setAge(updateRequest.getAge());
        }
        
        if (updateRequest.getPassword() != null && updateRequest.getPassword().length() >= 8) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        
        if (updateRequest.getgovernorate() != null && !updateRequest.getgovernorate().trim().isEmpty()) {
            user.setgovernorate(updateRequest.getgovernorate()); // Corrected setter name
        
        }

        User updatedUser = userRepository.save(user);
        String newToken = jwtUtil.generateToken(updatedUser.getEmail());

        return ResponseEntity.ok(new AuthResponse(newToken, updatedUser));
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
            throw new InvalidEmailException();
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new WeakPasswordException();
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidNameException();
        }
        if (request.getAge() < 21) {
            throw new InvalidAgeException();
        }
        
        if (request.getgovernorate() == null || request.getgovernorate().trim().isEmpty()) {
            throw new InvalidAddressException(); // You might need to create this exception
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException();
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setgovernorate(request.getgovernorate());
        user.setRole("CLIENT");

        // Save user
        User savedUser = userRepository.save(user);

        MailWelcome mw = new MailWelcome(savedUser.getName(),savedUser.getEmail()) ;
        kafkaTemplate.send(topic, mw); 

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());
        
        // Return response with token and user data
        return ResponseEntity.status(201).body(new AuthResponse(token, savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(AccountNotFoundException::new);

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        
        // Return response with token and user data
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    // Inner class for login/register request
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest extends LoginRequest {
        private String name;
        private int age;
        private String governorate;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getgovernorate() { return governorate; }
        public void setgovernorate(String governorate) { this.governorate = governorate; }
    }

    public static class AuthResponse {
        private final String token;
        private final User user;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        // Getters
        public String getToken() { return token; }
        public User getUser() { return user; }
    }
    
    
    
    
    
    
    
    
    
    
    
}