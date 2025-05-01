package com.service_client.main.controller;

import com.service_client.main.model.Location;
import com.service_client.main.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.service_client.main.config.AuthServiceClient;
import com.service_client.main.config.AuthServiceClient.AuthResponse;

import jakarta.servlet.http.HttpServletRequest; // Import for HttpServletRequest
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

	private final LocationService locationService;
    private final AuthServiceClient authServiceClient; 

    public LocationController(LocationService locationService, AuthServiceClient authServiceClient) {
        this.locationService = locationService;
        this.authServiceClient = authServiceClient;
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(HttpServletRequest request, @RequestBody LocationRequestDTO dto) {
        try {
            Long clientId = (Long) request.getAttribute("clientId");
            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String token = extractTokenFromHeader(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            if (dto == null || dto.getCarId() == null || dto.getStartDate() == null || dto.getEndDate() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            LocalDate startDate = parseLocalDate(dto.getStartDate());
            LocalDate endDate = parseLocalDate(dto.getEndDate());
            if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
                return ResponseEntity.badRequest().body(null);
            }

            Location newLocation = locationService.createLocation(clientId, dto.getCarId(), startDate, endDate, "Bearer " + token);

            return ResponseEntity.status(HttpStatus.CREATED).body(newLocation);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<List<Location>> getMyLocations(HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Location> locations = locationService.getLocationsByClientId(clientId);
        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(locations);
    }

    
    @GetMapping("/testAuth")
    public ResponseEntity<AuthResponse> testAuth(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            AuthResponse authResponse = authServiceClient.validateToken("Bearer " + token);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    // Helper method to parse LocalDate
    private LocalDate parseLocalDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    static class LocationRequestDTO {
        private Long carId;
        private String startDate;
        private String endDate;

        public Long getCarId() {
            return carId;
        }

        public void setCarId(Long carId) {
            this.carId = carId;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
