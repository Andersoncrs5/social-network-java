package com.blog.writeapi.utils.result;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Function;

@Getter
public class Result<T> {
    private final boolean isSuccess;
    private final T value;
    private final Error error;
    private final HttpStatus status;

    private Result(boolean isSuccess, T value, Error error, HttpStatus status) {
        this.isSuccess = isSuccess;
        this.value = value;
        this.error = error;
        this.status = status;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(true, value, Error.NONE, HttpStatus.OK);
    }

    public static <T> Result<T> created(T value) {
        return new Result<>(true, value, Error.NONE, HttpStatus.CREATED);
    }

    public static <T> Result<T> success(T value) {
        return ok(value);
    }

    public static <T> Result<T> failure(HttpStatus status, String code, String message) {
        return new Result<>(false, null, new Error(code, message, status.value()), status);
    }

    public static <T> Result<T> failure(Error error) {
        HttpStatus resolvedStatus = HttpStatus.resolve(error.status());
        return new Result<>(false, null, error, resolvedStatus != null ? resolvedStatus : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> Result<T> notFound(String message) {
        return failure(HttpStatus.NOT_FOUND, "Resource.NotFound", message);
    }

    public static <T> Result<T> conflict(String message) {
        return failure(HttpStatus.CONFLICT, "Resource.Conflict", message);
    }

    public static <T> Result<T> badRequest(String message) {
        return failure(HttpStatus.BAD_REQUEST, "Invalid.Input", message);
    }

    public static <T> Result<T> unprocessableEntity(String message) {
        return failure(HttpStatus.UNPROCESSABLE_ENTITY, "Validation.Error", message);
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public Optional<T> toOptional() {
        return isSuccess ? Optional.ofNullable(value) : Optional.empty();
    }

    public T getValueOr(T defaultValue) {
        return isSuccess ? value : defaultValue;
    }

    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (isFailure()) {
            return new Result<>(false, null, this.error, this.status);
        }
        return Result.ok(mapper.apply(value));
    }
}