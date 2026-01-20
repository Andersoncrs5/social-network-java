package com.blog.writeapi.services.interfaces;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.net.URL;
import java.util.List;

public interface IStorageService {
    void bucketExists(@NotBlank String bucket);
    Boolean putObject(
            @NotBlank String bucket,
            @NotBlank String key,
            @NotNull ObjectCannedACL acl,
            @NotNull MultipartFile file,
            @IsModelInitialized UserModel user
    );
    Boolean deleteObject(
            @NotBlank String bucketName,
            @NotBlank String key,
            String versionID
    );
    void deleteMultiObject(
            @NotBlank String bucketName,
            List<String> keys
    );
    URL generateLinkToDownload(
            @NotBlank String bucketName,
            @NotBlank String key,
            @NotNull long expirationDays
    );
    ResponseEntity<StreamingResponseBody> downloadStream(
            @NotBlank String bucket,
            @NotBlank String key
    );
    String generatePreSignedUploadUrl(
            @NotBlank String bucket,
            @NotBlank String key,
            @NotBlank String contentType,
            @NotNull long expirationMinutes
    );
    HeadObjectResponse getMetadata(
            @NotBlank String bucket,
            @NotBlank String key
    );
    void moveObject(
            @NotBlank String sourceBucket,
            @NotBlank String sourceKey,
            @NotBlank String destBucket,
            @NotBlank String destKey
    );
    boolean exists(
            @NotBlank String bucket,
            @NotBlank String key
    );
}
