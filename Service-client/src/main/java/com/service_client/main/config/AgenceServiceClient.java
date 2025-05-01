package com.service_client.main.config;

import com.service_client.main.model.CarDTO; 
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-agence")

public interface AgenceServiceClient {

    @GetMapping("/api/cars/{carId}")
    CarDTO getCarById(@PathVariable("carId") Long carId, @RequestHeader("Authorization") String token);
}