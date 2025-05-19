package com.example.review_service.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.review_service.domain.Review;
import com.example.review_service.domain.ReviewFlag;
import com.example.review_service.domain.ReviewLike;
import com.example.review_service.dto.ReviewRequestDto;
import com.example.review_service.dto.ReviewResponseDto;
import com.example.review_service.exception.BadRequestException;
import com.example.review_service.exception.NotFoundException;
import com.example.review_service.repository.ReviewFlagRepository;
import com.example.review_service.repository.ReviewLikeRepository;
import com.example.review_service.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ReviewLikeRepository likeRepo;
    private final ReviewFlagRepository flagRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         ReviewLikeRepository likeRepo,
                         ReviewFlagRepository flagRepo) {
        this.reviewRepo = reviewRepo;
        this.likeRepo = likeRepo;
        this.flagRepo = flagRepo;
    }

    @Transactional
    public ReviewResponseDto create(UUID userId, ReviewRequestDto dto) {

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("rating must be 1-5");
        }
        UUID id = UUID.randomUUID();
        Review r = new Review(id, userId,
                              dto.getRestaurantId(),
                              dto.getRating(),
                              dto.getContent(),
                              OffsetDateTime.now());
        reviewRepo.save(r);
        return map(r, userId);
    }

    @Transactional
    public void toggleLike(UUID userId, UUID reviewId) {
        Optional<ReviewLike> existing = likeRepo.findByUserIdAndReviewId(userId, reviewId);
        if (existing.isPresent()) {
            likeRepo.delete(existing.get());
        } else {
            likeRepo.save(new ReviewLike(UUID.randomUUID(), userId, reviewId, OffsetDateTime.now()));
        }
    }



    @Transactional
    public void toggleFlag(UUID userId, UUID reviewId, String reason) {
        Optional<ReviewFlag> existing = flagRepo.findByUserIdAndReviewId(userId, reviewId);
        if (existing.isPresent()) {
            flagRepo.delete(existing.get());
        } else {
            flagRepo.save(new ReviewFlag(UUID.randomUUID(), userId, reviewId, reason, OffsetDateTime.now()));
        }
    }

    public List<ReviewResponseDto> byRestaurant(String restaurantId, UUID me) {
        List<Object[]> rows = reviewRepo.findAllWithUserNameNative(restaurantId);
        List<ReviewResponseDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            ReviewResponseDto dto = new ReviewResponseDto();
            dto.setId((UUID) row[0]);
            dto.setUserId((UUID) row[1]);
            dto.setRestaurantId((String) row[2]);
            dto.setRating((Integer) row[3]);
            dto.setContent((String) row[4]);
            dto.setCreatedAt(((Instant) row[5]).atOffset(ZoneOffset.UTC));
            dto.setUserName((String) row[6]);

            dto.setLikeCount(likeRepo.countByReviewId(dto.getId()));
            if (me != null) {
                dto.setLikedByMe(likeRepo.findByUserIdAndReviewId(me, dto.getId()).isPresent());
                dto.setFlaggedByMe(flagRepo.findByUserIdAndReviewId(me, dto.getId()).isPresent());
            }

            result.add(dto);
        }

        return result;
    }



    public List<ReviewResponseDto> byUser(UUID userId, UUID me) {
        return reviewRepo.findByUserIdOrderByCreatedAtDesc(userId)
                         .stream()
                         .map(r -> map(r, me))
                         .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        Review review = reviewRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        reviewRepo.delete(review);
    }


    /* ---------- helpers ---------- */
    private ReviewResponseDto map(Review r, UUID me) {

        ReviewResponseDto d = new ReviewResponseDto();
        d.setId(r.getId());
        d.setUserId(r.getUserId());
        d.setRestaurantId(r.getRestaurantId());
        d.setRating(r.getRating());
        d.setContent(r.getContent());
        d.setCreatedAt(r.getCreatedAt());

        d.setLikeCount(likeRepo.countByReviewId(r.getId()));
        if (me != null) {
            d.setLikedByMe(likeRepo.findByUserIdAndReviewId(me, r.getId()).isPresent());
            d.setFlaggedByMe(flagRepo.findByUserIdAndReviewId(me, r.getId()).isPresent());
        }
        return d;
    }

    public boolean isReviewOwner(UUID userId, UUID reviewId) {
    return reviewRepo.findById(reviewId)
            .map(review -> review.getUserId().equals(userId))
            .orElse(false); // Or throw NotFoundException if stricter
    }

    public List<ReviewResponseDto> getFlaggedReviews() {
        List<Review> reviews = reviewRepo.findFlaggedReviews();
        return reviews.stream()
                    .map(r -> map(r, null)) // use `null` if admin has no ID
                    .toList();
    }

    @Transactional
    public void deleteFlaggedReview(UUID reviewId) {
        Review review = reviewRepo.findById(reviewId)
            .orElseThrow(() -> new NotFoundException("Review not found"));
        
        // Optionally: delete flags too
        flagRepo.deleteByReviewId(reviewId);
        
        reviewRepo.delete(review);
    }



}
