package com.blog.writeapi.services.providers;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.interfaces.IStorageService;
import com.blog.writeapi.utils.annotations.valid.isModelInitialized.IsModelInitialized;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService implements IStorageService {

    private final S3Client client;
    private final S3Presigner presigner;

    public void bucketExists(@NotBlank String bucket) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bucket not found");
        }
    }

    public Boolean putObject(
            @NotBlank String bucket,
            @NotBlank String key,
            @NotNull ObjectCannedACL acl,
            @NotNull MultipartFile file,
            @IsModelInitialized UserModel user
    ) {

        Map<String, String> DataMaps = new HashMap<>();
        DataMaps.put("uploader", user.getEmail());
        DataMaps.put("size", String.valueOf(file.getSize()));

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(DataMaps)
                .acl(acl)
                .contentType(file.getContentType())
                .build();

        try {
            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public Boolean deleteObject(
            @NotBlank String bucketName,
            @NotBlank String key,
            String versionID
    ) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .versionId((versionID != null && !versionID.isBlank()) ? versionID : null)
                .build();

        DeleteObjectResponse response = client.deleteObject(request);

        return response.sdkHttpResponse().isSuccessful();
    }

    public void deleteMultiObject(
            @NotBlank String bucketName,
            List<String> keys
    ) {
        List<ObjectIdentifier> objectsToDelete = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(d -> d.objects(objectsToDelete))
                .build();

        client.deleteObjects(request);
    }

    public URL generateLinkToDownload(
            @NotBlank String bucketName,
            @NotBlank String key,
            @NotNull long expirationDays
    ) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofDays(expirationDays))
                .build();

        return presigner.presignGetObject(presignRequest).url();
    }

    public ResponseEntity<StreamingResponseBody> downloadStream(
            @NotBlank String bucket,
            @NotBlank String key
    ) {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        ResponseInputStream<GetObjectResponse> s3Stream = client.getObject(request);

        StreamingResponseBody responseBody = outputStream -> {
            try (s3Stream) {
                s3Stream.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s3Stream.response().contentType()))
                .body(responseBody);
    }

    public String generatePreSignedUploadUrl(
            @NotBlank String bucket,
            @NotBlank String key,
            @NotBlank String contentType,
            @NotNull long expirationMinutes
    ) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public HeadObjectResponse getMetadata(
            @NotBlank String bucket,
            @NotBlank String key
    ) {
        return client.headObject(HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    public void moveObject(
            @NotBlank String sourceBucket,
            @NotBlank String sourceKey,
            @NotBlank String destBucket,
            @NotBlank String destKey
    ) {
        client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(destBucket)
                .destinationKey(destKey)
                .build());

        client.deleteObject(DeleteObjectRequest.builder().bucket(sourceBucket).key(sourceKey).build());
    }

    public boolean exists(
            @NotBlank String bucket,
            @NotBlank String key
    ) {
        try {
            client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

}
