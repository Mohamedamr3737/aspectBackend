package com.example.restaurant_service.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restaurant_service.entity.MostVisitedRestaurant;

@Repository
public interface MostVisitedRepository extends JpaRepository<MostVisitedRestaurant, UUID> {
    Optional<MostVisitedRestaurant> findByPlaceId(String placeId);

    List<MostVisitedRestaurant> findTop4ByOrderByVisitCountDesc();

}
