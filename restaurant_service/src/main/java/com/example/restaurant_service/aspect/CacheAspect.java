package com.example.restaurant_service.aspect;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.restaurant_service.dto.PlaceDetails;
import com.example.restaurant_service.entity.MostVisitedRestaurant;
import com.example.restaurant_service.repository.MostVisitedRepository;
@Aspect
@Component
public class CacheAspect {
    private final Executor executor = Executors.newCachedThreadPool();
    private static final long SEARCH_TTL_SECONDS = 60 * 60;      // 1 hour
    private static final long DETAILS_TTL_SECONDS = 60 * 60 * 24; // 1 day
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MostVisitedRepository repo;
    
    @Around("execution(* com.example.restaurant_service.service.PlacesService.searchPlaces(..))")
    public Object cacheSearch(ProceedingJoinPoint joinPoint) throws Throwable {
        String query = (String) joinPoint.getArgs()[0];
        String key = "search:" + query;
        Object cachedResult = redisTemplate.opsForValue().get(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        Object result = joinPoint.proceed();
        redisTemplate.opsForValue().set(key, result, Duration.ofSeconds(SEARCH_TTL_SECONDS));
        return result;
    }

   @Around("execution(* com.example.restaurant_service.service.PlacesService.getPlaceDetails(..))")
    public Object cacheDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String placeId = (String) joinPoint.getArgs()[0];
        String key = "details:" + placeId;

        Object cachedResult = redisTemplate.opsForValue().get(key);

        // Track visit (async) even from cache
        trackVisitAsync(placeId, cachedResult);

        if (cachedResult != null) {
            return cachedResult;
        }

        Object result = joinPoint.proceed();

        // Save to cache
        redisTemplate.opsForValue().set(key, result, Duration.ofSeconds(DETAILS_TTL_SECONDS));

        // Track visit (async) for fresh result
        trackVisitAsync(placeId, result);

        return result;
    }

    private void trackVisitAsync(String placeId, Object result) {
        if (result instanceof PlaceDetails details) {
            executor.execute(() -> {
                String name = details.getName();
                repo.findByPlaceId(placeId).ifPresentOrElse(existing -> {
                    existing.setVisitCount(existing.getVisitCount() + 1);
                    existing.setLastVisitedAt(LocalDateTime.now());
                    repo.save(existing);
                }, () -> {
                    MostVisitedRestaurant entry = new MostVisitedRestaurant();
                    entry.setPlaceId(placeId);
                    entry.setName(name);
                    entry.setVisitCount(1);
                    entry.setLastVisitedAt(LocalDateTime.now());
                    repo.save(entry);
                });
            });
        }
    }

}
