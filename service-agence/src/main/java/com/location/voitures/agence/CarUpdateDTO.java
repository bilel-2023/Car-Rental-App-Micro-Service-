package com.location.voitures.agence;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;

public class CarUpdateDTO {

    @Size(min = 2, max = 100)
    private String brand;

    @Size(min = 2, max = 100)
    private String model;

    private String releaseDate; // Allow updating the release date

    @Positive(message = "Price per day must be positive")
    private BigDecimal pricePerDay;

    private MultipartFile[] imageFiles;

    // Default constructor (required for Spring)
    public CarUpdateDTO() {
    }

    // Constructor with all fields (optional)
    public CarUpdateDTO(String brand, String model, String releaseDate, BigDecimal pricePerDay, MultipartFile[] imageFiles) {
        this.brand = brand;
        this.model = model;
        this.releaseDate = releaseDate;
        this.pricePerDay = pricePerDay;
        this.imageFiles = imageFiles;
    }

    // Getters
    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public MultipartFile[] getImageFiles() {
        return imageFiles;
    }

    // Setters
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setImageFiles(MultipartFile[] imageFiles) {
        this.imageFiles = imageFiles;
    }
}