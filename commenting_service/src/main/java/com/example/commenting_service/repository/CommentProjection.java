package com.example.commenting_service.repository;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface CommentProjection {
    UUID getId();
    UUID getUserId();
    UUID getReviewId();
    String getContent();
    OffsetDateTime getCreatedAt();
    long getLikeCount();
    boolean isLikedByMe();
    boolean isFlaggedByMe();
    String getUserName();
}
