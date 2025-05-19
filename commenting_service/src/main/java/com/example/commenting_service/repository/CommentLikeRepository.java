package com.example.commenting_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.commenting_service.domain.CommentLike;
// CommentLikeRepository

public interface CommentLikeRepository extends JpaRepository<CommentLike,UUID>{
    Optional<CommentLike> findByUserIdAndCommentId(UUID userId,UUID commentId);
    long countByCommentId(UUID commentId);
}
