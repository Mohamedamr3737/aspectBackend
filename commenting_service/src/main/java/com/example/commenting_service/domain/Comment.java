// Comment.java
package com.example.commenting_service.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="comments")
public class Comment {
    @Id @Column(columnDefinition="uuid")
    private UUID id;
    @Column(name="review_id",columnDefinition="uuid",nullable=false)
    private UUID reviewId;
    @Column(name="user_id",columnDefinition="uuid",nullable=false)
    private UUID userId;
    @Column(columnDefinition="text",nullable=false)
    private String content;
    @Column(name="created_at",nullable=false)
    private OffsetDateTime createdAt;

    public Comment() {}
    public Comment(UUID id,UUID reviewId,UUID userId,String content,OffsetDateTime createdAt){
        this.id=id; this.reviewId=reviewId; this.userId=userId;
        this.content=content; this.createdAt=createdAt;
    }

    public UUID getId(){return id;}
    public UUID getReviewId(){return reviewId;}
    public UUID getUserId(){return userId;}
    public String getContent(){return content;}
    public OffsetDateTime getCreatedAt(){return createdAt;}
}
