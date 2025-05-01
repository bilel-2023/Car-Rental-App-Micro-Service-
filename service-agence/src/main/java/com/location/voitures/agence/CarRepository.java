package com.location.voitures.agence;

import com.location.voitures.agence.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByAgencyId(Long agencyId);
    List<Car> findByGov(String gov); 

}