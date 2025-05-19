package com.example.commenting_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.commenting_service.annotation.IsAdmin;
import com.example.commenting_service.annotation.RequireAuth;
import com.example.commenting_service.aspect.IsCommentOwner;
import com.example.commenting_service.dto.CommentRequestDto;
import com.example.commenting_service.dto.CommentResponseDto;
import com.example.commenting_service.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController @RequestMapping("/api")
public class CommentController {

    private final CommentService svc;
    public CommentController(CommentService svc){this.svc=svc;}

    @PostMapping("/reviews/{reviewId}/comments")
    @RequireAuth
    public ResponseEntity<CommentResponseDto> create(HttpServletRequest req,
            @PathVariable UUID reviewId,@RequestBody CommentRequestDto dto){
        UUID uid=(UUID)req.getAttribute("userId");
        return ResponseEntity.ok(svc.create(uid,reviewId,dto));
    }

    @GetMapping("/reviews/{reviewId}/comments")
    @RequireAuth    
    public List<CommentResponseDto> list(HttpServletRequest req,@PathVariable UUID reviewId){
        return svc.byReview(reviewId,(UUID)req.getAttribute("userId"));
    }


    @PostMapping("/comments/{id}/like") @RequireAuth
    public ResponseEntity<Void> toggleLike(HttpServletRequest req,@PathVariable UUID id){
        svc.toggleLike((UUID)req.getAttribute("userId"),id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/comments/{id}/flag") @RequireAuth
    public ResponseEntity<Void> toggleFlag(HttpServletRequest req,@PathVariable UUID id,
                                           @RequestParam(required=false) String reason){
        svc.toggleFlag((UUID)req.getAttribute("userId"),id,reason);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{id}")
    @RequireAuth 
    @IsCommentOwner
    public ResponseEntity<Void> delete(HttpServletRequest req,@PathVariable UUID id){
        svc.delete(id); return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments/flagged")
    @RequireAuth
    @IsAdmin
    public ResponseEntity<?> flaggedComments() {
        return ResponseEntity.ok(svc.getFlaggedComments());
    }



    @DeleteMapping("/comments/flagged/{id}")
    @RequireAuth
    public ResponseEntity<Void> deleteFlagged(@PathVariable UUID id) {
        svc.deleteFlaggedComment(id);
        return ResponseEntity.noContent().build();
    }


}
