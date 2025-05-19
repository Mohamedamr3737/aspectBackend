package com.example.restaurant_service.aspect;

import java.time.LocalDateTime;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.restaurant_service.dto.PlaceDetails;
import com.example.restaurant_service.entity.MostVisitedRestaurant;
import com.example.restaurant_service.repository.MostVisitedRepository;

@Aspect
@Component
public class MostVisitedAspect {

    private final MostVisitedRepository repo;

    public MostVisitedAspect(MostVisitedRepository repo) {
        this.repo = repo;
    }

@AfterReturning(
    pointcut = "execution(* com.example.restaurant_service.service.PlacesService.getPlaceDetails(..))",
    returning = "details")
public void trackVisits(JoinPoint jp, Object details) {
    String placeId = null;
    String name = null;
    String imageUrl = null;
    Integer priceLevel = null;

    if (details instanceof PlaceDetails d) {
        placeId = d.getPlaceId();
        name = d.getName();
        imageUrl = d.getImageUrl(); // may be null
        priceLevel = d.getPriceLevel(); // int, boxed into Integer
    } else if (details instanceof Map<?, ?> map) {
        Object pid = map.get("placeId");
        Object nm = map.get("name");
        Object img = map.get("imageUrl");
        Object price = map.get("priceLevel");

        if (pid != null && nm != null) {
            placeId = pid.toString();
            name = nm.toString();
            imageUrl = (img != null) ? img.toString() : null;
            if (price != null) {
                try {
                    priceLevel = Integer.parseInt(price.toString());
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    if (placeId == null || name == null) return;

    final String finalPlaceId = placeId;
    final String finalName = name;
    final String finalImage = imageUrl;
    final Integer finalPrice = priceLevel;

    repo.findByPlaceId(finalPlaceId).ifPresentOrElse(existing -> {
        existing.setVisitCount(existing.getVisitCount() + 1);
        existing.setLastVisitedAt(LocalDateTime.now());
        repo.saveAndFlush(existing);
    }, () -> {
        MostVisitedRestaurant newEntry = new MostVisitedRestaurant();
        newEntry.setPlaceId(finalPlaceId);
        newEntry.setName(finalName);
        newEntry.setImage(finalImage); // may be null
        newEntry.setPriceLevel(finalPrice); // may be null
        newEntry.setVisitCount(1);
        newEntry.setLastVisitedAt(LocalDateTime.now());
        repo.save(newEntry);
    });
}

}
