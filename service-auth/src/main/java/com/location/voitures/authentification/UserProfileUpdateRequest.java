package com.location.voitures.authentification;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UserProfileUpdateRequest {

    @Size(min = 2, max = 100)
    private String name;

    @Min(21)
    private Integer age;

    private String gov;

    @Size(min = 8)
    private String password;

    // Default constructor
    public UserProfileUpdateRequest() {
    }

    // Getters
    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getgovernorate() {
        return gov;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setgovernorate(String gov) {
        this.gov = gov;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}