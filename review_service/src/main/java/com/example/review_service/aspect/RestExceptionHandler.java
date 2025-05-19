package com.example.review_service.aspect;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.review_service.exception.BadRequestException;
import com.example.review_service.exception.ConflictException;
import com.example.review_service.exception.ForbiddenException;
import com.example.review_service.exception.NotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> bad(BadRequestException ex){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> nf(NotFoundException ex){
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String,Object>> conflict(ConflictException ex){
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String,Object>> forbidden(ForbiddenException ex){
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> handleStatus(ResponseStatusException ex) {
        return build((HttpStatus) ex.getStatusCode(), ex.getReason());
    }

    private ResponseEntity<Map<String,Object>> build(HttpStatus status,String msg){
        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", msg);
        return ResponseEntity.status(status).body(body);
    }
}
