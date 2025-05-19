package com.example.review_service.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "review_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","review_id"}))
public class ReviewLike {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "review_id", nullable = false, columnDefinition = "uuid")
    private UUID reviewId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ReviewLike() { }

    public ReviewLike(UUID id, UUID userId, UUID reviewId, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.reviewId = reviewId;
        this.createdAt = createdAt;
    }

    /* getters & setters */
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
