// CommentRepository
package com.example.commenting_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.commenting_service.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment,UUID>{
    List<Comment> findByReviewIdOrderByCreatedAtAsc(UUID reviewId);
    @Query("SELECT c FROM Comment c WHERE c.id IN (SELECT f.commentId FROM CommentFlag f GROUP BY f.commentId)")
    List<Comment> findFlaggedComments();
    void deleteById(UUID id);

@Query(value = """
    SELECT 
        c.id,
        c.user_id,
        c.review_id,
        c.content,
        c.created_at,
        COALESCE(cl.like_count, 0),
        CASE WHEN clm.user_id IS NOT NULL THEN true ELSE false END,
        CASE WHEN cf.user_id IS NOT NULL THEN true ELSE false END,
        u.name
    FROM comments c
    LEFT JOIN (
        SELECT comment_id, COUNT(*) AS like_count
        FROM comment_likes
        GROUP BY comment_id
    ) cl ON c.id = cl.comment_id
    LEFT JOIN comment_likes clm ON c.id = clm.comment_id AND clm.user_id = :userId
    LEFT JOIN comment_flags cf ON c.id = cf.comment_id AND cf.user_id = :userId
    LEFT JOIN users u ON c.user_id = u.id
    WHERE c.review_id = :reviewId
    ORDER BY c.created_at DESC
""", nativeQuery = true)
List<Object[]> findByReviewIdOrderByCreatedAtDescRaw(
    @Param("reviewId") UUID reviewId,
    @Param("userId") UUID userId
);




}
