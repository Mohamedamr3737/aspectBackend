package com.example.review_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.review_service.domain.Review;

import io.lettuce.core.dynamic.annotation.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId);
    List<Review> findByUserIdOrderByCreatedAtDesc(UUID userId);
    @Query("SELECT r FROM Review r WHERE r.id IN (" +
        "SELECT f.reviewId FROM ReviewFlag f GROUP BY f.reviewId)")
    List<Review> findFlaggedReviews();

    @Query(value = """
    SELECT r.id, r.user_id, r.restaurant_id, r.rating, r.content, r.created_at, u.name as user_name
    FROM reviews r
    JOIN users u ON r.user_id = u.id
    WHERE r.restaurant_id = :restaurantId
    ORDER BY r.created_at DESC
    """, nativeQuery = true)
    List<Object[]> findAllWithUserNameNative(@Param("restaurantId") String restaurantId);


}
