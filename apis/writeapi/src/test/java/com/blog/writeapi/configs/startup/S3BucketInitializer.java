package com.blog.writeapi.configs.startup;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@Profile("test")
@RequiredArgsConstructor
public class S3BucketInitializer {

    private final S3Client s3Client;

    @Value("${s3.bucketAttachments}")
    private String bucket;

    @PostConstruct
    public void init() {
        if (!bucketExists(bucket)) {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build());
        }
    }

    private boolean bucketExists(String bucket) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucket)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}
