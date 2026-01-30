package com.blog.writeapi.configs.exception;

import com.blog.writeapi.utils.exceptions.ModelNotFoundException;
import com.blog.writeapi.utils.exceptions.ResourceOwnerMismatchException;
import com.blog.writeapi.utils.res.ResponseHttp;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseHttp<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new ResponseHttp<>(
                null,
                "File is too large! Maximum limit is 10MB.",
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(ResourceOwnerMismatchException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleResourceOwnerMismatchException(ResourceOwnerMismatchException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseHttp<>(
                null,
                ex.getMessage(),
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleS3Exception(S3Exception ex) {
        log.error("S3 Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseHttp<>(
                null,
                "Storage service error: " + ex.getMessage(),
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(NoSuchBucketException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleNoSuchBucketException(NoSuchBucketException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseHttp<>(
                null,
                "Storage bucket not found.",
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(NoSuchKeyException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleNoSuchKeyException(NoSuchKeyException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseHttp<>(
                null,
                "The requested file does not exist in our storage.",
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleModelNotFoundException(ModelNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseHttp<>(
                null,
                ex.getMessage(),
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHttp<>(
                null,
                "This record was updated by another user. Please refresh and try again.",
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ResponseHttp<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseHttp<>(
                null,
                "Database integrity violation: " + ex.getMostSpecificCause().getMessage(),
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ResponseHttp<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ex.getBindingResult().getGlobalErrors().forEach(error ->
                errors.put("global", error.getDefaultMessage()));

        boolean isConflict = ex.getBindingResult().getGlobalErrors().stream()
                .anyMatch(error -> error.getCode() != null && error.getCode().contains("UniquePrimaryCategoryInPost"));

        HttpStatus status = isConflict ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ResponseHttp<>(
                errors,
                "Validation failed",
                UUID.randomUUID().toString(),
                0,
                false,
                OffsetDateTime.now()
        ));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<@NonNull ResponseHttp<Object>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseHttp<>(
                        null,
                        "System temporarily overloaded (Circuit Breaker Open).",
                        UUID.randomUUID().toString(),
                        0,
                        false,
                        OffsetDateTime.now()
                ));
    }

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
