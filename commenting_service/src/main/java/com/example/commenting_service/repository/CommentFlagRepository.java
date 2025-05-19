package com.example.commenting_service.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.commenting_service.domain.CommentFlag;

// CommentFlagRepository

public interface CommentFlagRepository extends JpaRepository<CommentFlag,UUID>{
    Optional<CommentFlag> findByUserIdAndCommentId(UUID userId,UUID commentId);
    long countByCommentId(UUID commentId);
    void deleteByCommentId(UUID commentId);
}