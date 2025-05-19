package com.example.restaurant_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_service.dto.PlaceDetails;
import com.example.restaurant_service.dto.PlaceSummary;
import com.example.restaurant_service.entity.MostVisitedRestaurant;
import com.example.restaurant_service.repository.MostVisitedRepository;
import com.example.restaurant_service.service.PlacesService;

@RestController
@RequestMapping("/places")
public class PlacesController {

    @Autowired
    private PlacesService placesService;
    @Autowired
    private MostVisitedRepository mostVisitedRepository; 
    @GetMapping("/search")
    public List<PlaceSummary> search(@RequestParam String query) {
        return placesService.searchPlaces(query);
    }

    @GetMapping("/details")
    public PlaceDetails details(@RequestParam String placeId) {
        PlaceDetails details = placesService.getPlaceDetails(placeId);
        return details;
    }

    @GetMapping("/most-visited")
    public List<MostVisitedRestaurant> mostVisited() {
        return mostVisitedRepository.findTop4ByOrderByVisitCountDesc();
    }

}
