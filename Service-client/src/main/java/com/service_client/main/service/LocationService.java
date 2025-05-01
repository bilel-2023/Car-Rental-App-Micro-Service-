package com.service_client.main.service;

import com.service_client.main.config.AgenceServiceClient;
import com.service_client.main.model.CarDTO;
import com.service_client.main.model.Location;
import com.service_client.main.LocationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final AgenceServiceClient agenceServiceClient;

    public LocationService(LocationRepository locationRepository, AgenceServiceClient agenceServiceClient) {
        this.locationRepository = locationRepository;
        this.agenceServiceClient = agenceServiceClient;
    }

    public Location createLocation(Long clientId, Long carId, LocalDate startDate, LocalDate endDate, String token) {
        CarDTO car = agenceServiceClient.getCarById(carId, token);
        if (car == null || car.getAgencyId() == null) {
            throw new RuntimeException("Could not retrieve agency ID for car: " + carId);
        }

        Long fetchedAgencyId = car.getAgencyId();

        Location newLocation = new Location();
        newLocation.setClientId(clientId);
        newLocation.setCarId(carId);
        newLocation.setStartDate(startDate);
        newLocation.setEndDate(endDate);
        newLocation.setAgencyId(fetchedAgencyId);
        return locationRepository.save(newLocation);
    }
    
    public List<Location> getLocationsByClientId(Long clientId) {
        return locationRepository.findByClientId(clientId);
    }

}
