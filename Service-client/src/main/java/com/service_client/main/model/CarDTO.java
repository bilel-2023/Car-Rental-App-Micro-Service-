package com.service_client.main.model;

import java.time.LocalDate;
import java.util.List;

public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private LocalDate releaseDate;
    private String gov;
    private List<String> imageUrls;
    private Long agencyId;
    private Double pricePerDay;
    private Boolean av;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGov() {
        return gov;
    }

    public void setGov(String gov) {
        this.gov = gov;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Boolean getAv() {
        return av;
    }

    public void setAv(Boolean av) {
        this.av = av;
    }
}
