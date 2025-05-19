package com.example.commenting_service.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.commenting_service.domain.Comment;
import com.example.commenting_service.domain.CommentFlag;
import com.example.commenting_service.domain.CommentLike;
import com.example.commenting_service.dto.CommentRequestDto;
import com.example.commenting_service.dto.CommentResponseDto;
import com.example.commenting_service.exception.NotFoundException;
import com.example.commenting_service.repository.CommentFlagRepository;
import com.example.commenting_service.repository.CommentLikeRepository;
import com.example.commenting_service.repository.CommentRepository;
@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final CommentLikeRepository likeRepo;
    private final CommentFlagRepository flagRepo;

    public CommentService(CommentRepository commentRepo,
                          CommentLikeRepository likeRepo,
                          CommentFlagRepository flagRepo){
        this.commentRepo=commentRepo;this.likeRepo=likeRepo;
        this.flagRepo=flagRepo;
    }

    @Transactional
    public CommentResponseDto create(UUID user,UUID review,CommentRequestDto dto){
        Comment c=new Comment(UUID.randomUUID(),review,user,dto.getContent(),OffsetDateTime.now());
        commentRepo.save(c);
        return map(c,user);
    }

    @Transactional
    public void toggleLike(UUID user,UUID comment){
        commentRepo.findById(comment).orElseThrow(()->new NotFoundException("comment not found"));
        likeRepo.findByUserIdAndCommentId(user,comment)
                .ifPresentOrElse(likeRepo::delete,
                    ()->likeRepo.save(new CommentLike(UUID.randomUUID(),user,comment,OffsetDateTime.now())));
    }

    @Transactional
    public void toggleFlag(UUID user,UUID comment,String reason){
        commentRepo.findById(comment).orElseThrow(()->new NotFoundException("comment not found"));
        flagRepo.findByUserIdAndCommentId(user,comment)
                .ifPresentOrElse(flagRepo::delete,
                    ()->flagRepo.save(new CommentFlag(UUID.randomUUID(),user,comment,reason,OffsetDateTime.now())));
    }

public List<CommentResponseDto> byReview(UUID reviewId, UUID userId) {
    List<Object[]> raw = commentRepo.findByReviewIdOrderByCreatedAtDescRaw(reviewId, userId);
    
    return raw.stream().map(new Function<Object[], CommentResponseDto>() {
        @Override
        public CommentResponseDto apply(Object[] row) {
            return new CommentResponseDto(
                    (UUID) row[0],
                    (UUID) row[1],
                    (UUID) row[2],
                    (String) row[3],
                    ((Instant) row[4]).atOffset(ZoneOffset.UTC), // <-- fix here
                    ((Number) row[5]).longValue(),
                    (Boolean) row[6],
                    (Boolean) row[7],
                    (String) row[8]
            );  }
    }).toList();
}




    public boolean isCommentOwner(UUID user,UUID comment){
        return commentRepo.findById(comment).map(c->c.getUserId().equals(user)).orElse(false);
    }

    @Transactional public void delete(UUID comment){
        Comment c=commentRepo.findById(comment).orElseThrow(()->new NotFoundException("comment not found"));
        commentRepo.delete(c);
    }

    public List<CommentResponseDto> getFlaggedComments() {
    return commentRepo.findFlaggedComments()
        .stream()
        .map(c -> map(c, null))
        .toList();
    }

@Transactional
public void deleteFlaggedComment(UUID commentId) {
    flagRepo.deleteByCommentId(commentId);
    commentRepo.deleteById(commentId);
}

    /* helper */
    private CommentResponseDto map(Comment c,UUID me){
        CommentResponseDto d=new CommentResponseDto();
        d.setId(c.getId());d.setReviewId(c.getReviewId());d.setUserId(c.getUserId());
        d.setContent(c.getContent());d.setCreatedAt(c.getCreatedAt());
        d.setLikeCount(likeRepo.countByCommentId(c.getId()));
        if(me!=null){
            d.setLikedByMe(likeRepo.findByUserIdAndCommentId(me,c.getId()).isPresent());
            d.setFlaggedByMe(flagRepo.findByUserIdAndCommentId(me,c.getId()).isPresent());
        }
        return d;
    }
}
