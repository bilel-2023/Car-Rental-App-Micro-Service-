package com.location.voitures.agence.Service;

import com.location.voitures.agence.AuthServiceClient;
import com.location.voitures.agence.Car;
import com.location.voitures.agence.CarDTO;
import com.location.voitures.agence.CarRepository;
import com.location.voitures.agence.CarUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class CarService {
    private final CarRepository carRepository;
    private final FileStorageService fileStorageService;
    private final AuthServiceClient authServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    public CarService(CarRepository carRepository, FileStorageService fileStorageService, AuthServiceClient authServiceClient) {
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
        this.authServiceClient = authServiceClient;
    }

    public Car addCarWithImages(@Valid CarDTO carDTO, HttpServletRequest request) {
        // Retrieve agencyId and userGov from the request attributes set by JwtForwardFilter
        Long agencyId = (Long) request.getAttribute("agencyId");
        String userGov = (String) request.getAttribute("userGov");

        if (agencyId == null) {
            throw new IllegalStateException("Agency ID not found in request");
        }
        if (userGov == null) {
            throw new IllegalStateException("User governorate not found in request");
        }

        if (carDTO.getReleaseDate() == null || carDTO.getReleaseDate().isEmpty()) {
            throw new IllegalArgumentException("Release date is required");
        }

        try {
            LocalDate releaseDate = LocalDate.parse(carDTO.getReleaseDate());

            Car car = new Car();
            car.setBrand(carDTO.getBrand());
            car.setModel(carDTO.getModel());
            car.setPricePerDay(carDTO.getPricePerDay());
            car.setReleaseDate(releaseDate);
            car.setAgencyId(agencyId);
            car.setAv(true);
            car.setGov(userGov); // Set the gov from the request attribute

            List<String> imageUrls = new ArrayList<>();
            if (carDTO.getImageFiles() != null && !carDTO.getImageFiles().isEmpty()) {
                if (carDTO.getImageFiles().size() > 5) {
                    throw new IllegalArgumentException("Cannot upload more than 5 images");
                }
                for (MultipartFile file : carDTO.getImageFiles()) {
                    if (!file.isEmpty()) {
                        String filename = fileStorageService.storeFile(file);
                        imageUrls.add("/images/" + filename);
                    }
                }
                car.setImageUrls(imageUrls);
            }

            logger.debug("Saving car with release date: {}", releaseDate);
            return carRepository.save(car);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please useyyyy-MM-DD format");
        }
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public boolean deleteCar(Long id) {
        Optional<Car> carToDeleteOptional = carRepository.findById(id);
        if (carToDeleteOptional.isEmpty()) {
            return false;
        }
        Car carToDelete = carToDeleteOptional.get();
        if (carToDelete.getImageUrls() != null) {
            for (String imageUrl : carToDelete.getImageUrls()) {
                String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                fileStorageService.deleteFile(filename);
            }
        }
        carRepository.deleteById(id);
        return true;
    }

    public Car updateCar(Long id, @Valid CarUpdateDTO carUpdateDTO) {
        Optional<Car> existingCarOptional = carRepository.findById(id);
        if (existingCarOptional.isEmpty()) {
            return null;
        }
        Car existingCar = existingCarOptional.get();
        if (carUpdateDTO.getBrand() != null) {
            existingCar.setBrand(carUpdateDTO.getBrand());
        }
        if (carUpdateDTO.getModel() != null) {
            existingCar.setModel(carUpdateDTO.getModel());
        }
        if (carUpdateDTO.getReleaseDate() != null && !carUpdateDTO.getReleaseDate().isEmpty()) {
            try {
                LocalDate releaseDate = LocalDate.parse(carUpdateDTO.getReleaseDate());
                existingCar.setReleaseDate(releaseDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please useyyyy-MM-DD format");
            }
        }
        if (carUpdateDTO.getPricePerDay() != null) {
            existingCar.setPricePerDay(carUpdateDTO.getPricePerDay());
        }
        if (carUpdateDTO.getImageFiles() != null && carUpdateDTO.getImageFiles().length > 0) {
            if (carUpdateDTO.getImageFiles().length > 5) {
                throw new IllegalArgumentException("Cannot upload more than 5 images");
            }
            if (existingCar.getImageUrls() != null) {
                for (String imageUrl : existingCar.getImageUrls()) {
                    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                    fileStorageService.deleteFile(filename);
                }
                existingCar.setImageUrls(new ArrayList<>());
            } else {
                existingCar.setImageUrls(new ArrayList<>());
            }
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : carUpdateDTO.getImageFiles()) {
                if (!file.isEmpty()) {
                    String filename = fileStorageService.storeFile(file);
                    imageUrls.add("/images/" + filename);
                }
            }
            existingCar.setImageUrls(imageUrls);
        }
        return carRepository.save(existingCar);
    }

    public List<Car> getCarsByAgencyId(Long agencyId) {
        return carRepository.findByAgencyId(agencyId);
    }
    
    
    public List<Car> getCarsByGov(String gov) {
        return carRepository.findByGov(gov);
    }
}