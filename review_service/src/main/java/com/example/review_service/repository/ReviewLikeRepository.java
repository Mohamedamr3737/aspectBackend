package com.example.review_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.review_service.domain.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {
    Optional<ReviewLike> findByUserIdAndReviewId(UUID userId, UUID reviewId);
    long countByReviewId(UUID reviewId);
}
