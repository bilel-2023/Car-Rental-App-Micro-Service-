package com.location.voitures.agence.controller;

import com.location.voitures.agence.Car;
import com.location.voitures.agence.CarDTO;
import com.location.voitures.agence.CarUpdateDTO;
import com.location.voitures.agence.Service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping(value = "/addcar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCar(@Valid CarDTO carDTO, HttpServletRequest request) {
        try {
            Car car = carService.addCarWithImages(carDTO, request); // Pass HttpServletRequest
            return ResponseEntity.ok(car);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating car with images");
        }
    }


    @PostMapping(value = "/upload-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTest(@RequestParam("file") MultipartFile file) {
        try {
            // Log file details
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCar(@PathVariable Long id, @Valid CarUpdateDTO carUpdateDTO, HttpServletRequest request) {
        try {
            Car updatedCar = carService.updateCar(id, carUpdateDTO);
            if (updatedCar != null) {
                return ResponseEntity.ok(updatedCar);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating car");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            boolean deleted = carService.deleteCar(id);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Car with ID " + id + " deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID " + id + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting car: " + e.getMessage());
        }
    }


    @GetMapping("/agency/me")
    public ResponseEntity<List<Car>> getMyCars(HttpServletRequest request) {
        Long agencyId = (Long) request.getAttribute("agencyId"); // Assuming agencyId is set in the request

        if (agencyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Or a more specific error message
        }

        List<Car> cars = carService.getCarsByAgencyId(agencyId);
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no cars for the agency
        }
        return ResponseEntity.ok(cars);
    }
    
    @GetMapping("/governorate/{gov}")
    public ResponseEntity<List<Car>> getCarsByGovernorate(@PathVariable String gov) {
        List<Car> cars = carService.getCarsByGov(gov);
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.ok(cars);
    }
}