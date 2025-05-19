package com.example.commenting_service.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// CommentFlag.java
@Entity @Table(name="comment_flags",
       uniqueConstraints=@UniqueConstraint(columnNames={"user_id","comment_id"}))
public class CommentFlag {
    @Id @Column(columnDefinition="uuid") private UUID id;
    @Column(name="user_id",columnDefinition="uuid",nullable=false) private UUID userId;
    @Column(name="comment_id",columnDefinition="uuid",nullable=false) private UUID commentId;
    @Column(columnDefinition="text") private String reason;
    @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
    public CommentFlag() {}
    public CommentFlag(UUID id,UUID userId,UUID commentId,String reason,OffsetDateTime ts){
        this.id=id;this.userId=userId;this.commentId=commentId;this.reason=reason;this.createdAt=ts;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
