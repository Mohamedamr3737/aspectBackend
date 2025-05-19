package com.example.review_service.dto;

public class ReviewRequestDto {

    private String restaurantId;
    private Integer rating;
    private String content;

    public ReviewRequestDto() { }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
