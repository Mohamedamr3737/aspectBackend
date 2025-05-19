package com.example.review_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.review_service.annotation.IsAdmin;
import com.example.review_service.annotation.RequireAuth;
import com.example.review_service.aspect.IsReviewOwner;
import com.example.review_service.dto.ReviewRequestDto;
import com.example.review_service.dto.ReviewResponseDto;
import com.example.review_service.service.ReviewService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/reviews")
    @RequireAuth
    public ResponseEntity<ReviewResponseDto> create(
            HttpServletRequest request,
            @RequestBody ReviewRequestDto dto) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @PostMapping("/reviews/{id}/like")
    @RequireAuth
    public ResponseEntity<Void> toggleLike(HttpServletRequest request,
                                     @PathVariable UUID id) {
        UUID userId = (UUID) request.getAttribute("userId");
        service.toggleLike(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reviews/{id}/flag")
    @RequireAuth
    public ResponseEntity<Void> toggleFlag(HttpServletRequest request,
                                     @PathVariable UUID id,
                                     @RequestParam(required = false) String reason) {
        UUID userId = (UUID) request.getAttribute("userId");
        service.toggleFlag(userId, id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurants/{restaurantId}/reviews")
    @RequireAuth
    public List<ReviewResponseDto> restaurantReviews(
            HttpServletRequest request,
            @PathVariable String restaurantId) {
        UUID userId = (UUID) request.getAttribute("userId"); // might be null
        return service.byRestaurant(restaurantId, userId);
    }

    @GetMapping("/users/{userId}/reviews")
    @RequireAuth
    public List<ReviewResponseDto> userReviews(
            HttpServletRequest request,
            @PathVariable UUID userId) {
        UUID viewerId = (UUID) request.getAttribute("userId"); // might be null
        return service.byUser(userId, viewerId);
    }

    @DeleteMapping("/reviews/{id}")
    @RequireAuth
    @IsReviewOwner
    public ResponseEntity<Void> deleteReview(
            HttpServletRequest request,
            @PathVariable UUID id) {
        UUID userId = (UUID) request.getAttribute("userId");
        service.delete(id); // only proceeds if user is owner
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/reviews/spam")
    @IsAdmin
    public List<ReviewResponseDto> getFlaggedReviews() {
        return service.getFlaggedReviews();
    }


    @DeleteMapping("/reviews/{id}/flagged")
    public ResponseEntity<Void> deleteFlaggedReview(@PathVariable UUID id) {
        service.deleteFlaggedReview(id);
        return ResponseEntity.noContent().build();
    }


}
