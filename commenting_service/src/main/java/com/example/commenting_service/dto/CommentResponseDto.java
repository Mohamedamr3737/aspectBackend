package com.example.commenting_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

// CommentResponseDto.java
public class CommentResponseDto {
    private UUID id,reviewId,userId;
    private String content;
    private OffsetDateTime createdAt;
    private long likeCount;
    private boolean likedByMe,flaggedByMe;
    private String userName;
    /* getters & setters */

    public CommentResponseDto(UUID id, UUID userId, UUID reviewId, String content,
                              OffsetDateTime createdAt, long likeCount,
                              boolean likedByMe, boolean flaggedByMe, String userName) {
        this.id = id;
        this.userId = userId;
        this.reviewId = reviewId;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.flaggedByMe = flaggedByMe;
        this.userName = userName;
    }

    public CommentResponseDto() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public boolean isFlaggedByMe() {
        return flaggedByMe;
    }

    public void setFlaggedByMe(boolean flaggedByMe) {
        this.flaggedByMe = flaggedByMe;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
