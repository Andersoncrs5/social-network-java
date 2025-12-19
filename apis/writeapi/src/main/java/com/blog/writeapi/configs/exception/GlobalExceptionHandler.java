package com.blog.writeapi.configs.exception;

import com.blog.writeapi.utils.res.ResponseHttp;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<@NonNull ResponseHttp<Object>> handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String property = violation.getPropertyPath().toString();
                    return property + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(" | "));

        ResponseHttp<Object> res = new ResponseHttp<>(
                null,
                message,
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
