package com.example.review_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ReviewResponseDto {

    private UUID id;
    private UUID userId;
    private String restaurantId;
    private Integer rating;
    private String content;
    private OffsetDateTime createdAt;
    private long likeCount;
    private boolean likedByMe;
    private boolean flaggedByMe;
    private String userName;

    public ReviewResponseDto() { }

    /* getters & setters */
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }

    public boolean isFlaggedByMe() { return flaggedByMe; }
    public void setFlaggedByMe(boolean flaggedByMe) { this.flaggedByMe = flaggedByMe; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ReviewResponseDto(UUID id, UUID userId, String restaurantId, Integer rating,
                         String content, OffsetDateTime createdAt, String userName) {
    this.id = id;
    this.userId = userId;
    this.restaurantId = restaurantId;
    this.rating = rating;
    this.content = content;
    this.createdAt = createdAt;
    this.userName = userName;
}

}
