package com.service_client.main;

import com.service_client.main.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // Import Optional

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findById(Long id); 
    List<Location> findByClientId(Long clientId);

}