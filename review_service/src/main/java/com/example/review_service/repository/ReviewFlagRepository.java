package com.example.review_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.review_service.domain.ReviewFlag;

public interface ReviewFlagRepository extends JpaRepository<ReviewFlag, UUID> {
    Optional<ReviewFlag> findByUserIdAndReviewId(UUID userId, UUID reviewId);
    void deleteByReviewId(UUID reviewId);

}
