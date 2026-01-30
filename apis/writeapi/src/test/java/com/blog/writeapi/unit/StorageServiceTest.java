package com.blog.writeapi.unit;

import com.blog.writeapi.models.UserModel;
import com.blog.writeapi.services.providers.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {

    @Mock
    private S3Client client;
    @Mock
    private S3Presigner presigner;

    @InjectMocks
    private StorageService service;

    private final String BUCKET = "images";
    private final String KEY = "1233454242";
    String CONTENT_TYPE = "image/jpeg";


    UserModel userMock = UserModel.builder()
            .id(1998780200074176609L)
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .version(1L)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    @Test
    void shouldReturnTrueWhenBucketExists() {
        HeadBucketResponse response = HeadBucketResponse.builder().build();
        when(client.headBucket(any(HeadBucketRequest.class))).thenReturn(response);

        this.service.bucketExists(BUCKET);

        verify(client, times(1)).headBucket(any(HeadBucketRequest.class));
    }

    @Test
    void shouldThrowNotFoundWhenBucketDoesNotExist() {
        when(client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder().message("Bucket not found").build());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> this.service.bucketExists(BUCKET)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(client, times(1)).headBucket(any(HeadBucketRequest.class));
    }

    @Test
    void shouldReturnTrueWhenUploadIsSuccessful() throws IOException {
        // Arrange
        String key = "test-key.png";
        byte[] content = "file content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", content);

        UserModel user = UserModel.builder().email("user@test.com").build();

        // Mock do S3 - putObject retorna um PutObjectResponse
        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Act
        Boolean result = service.putObject(BUCKET, key, ObjectCannedACL.PUBLIC_READ, file, user);

        // Assert
        assertThat(result).isTrue();

        // Verificação detalhada do Request (Captor ou ArgumentMatcher)
        verify(client).putObject(argThat((PutObjectRequest req) ->
                req.bucket().equals(BUCKET) &&
                        req.key().equals(key) &&
                        req.metadata().get("uploader").equals("user@test.com")
        ), any(RequestBody.class));
    }

    @Test
    void shouldReturnFalseWhenIOExceptionOccurs() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UserModel user = UserModel.builder().email("user@test.com").build();

        // Simula erro ao ler os bytes do arquivo
        when(file.getInputStream()).thenThrow(new IOException("Stream error"));
        when(file.getSize()).thenReturn(10L);

        // Act
        Boolean result = service.putObject(BUCKET, "key", ObjectCannedACL.PRIVATE, file, user);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(client);
    }

    @Test
    void shouldPropagateS3ExceptionWhenS3Fails() {
        // Arrange
        byte[] content = "content".getBytes();
        MultipartFile file = new MockMultipartFile("file", content);
        UserModel user = UserModel.builder().email("user@test.com").build();

        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Access Denied").build());

        // Act & Assert
        assertThrows(S3Exception.class, () ->
                service.putObject(BUCKET, "key", ObjectCannedACL.PRIVATE, file, user)
        );
    }

    @Test
    void shouldDeleteObjectWithoutVersionId() {
        // Arrange
        String key = "photo.jpg";
        DeleteObjectResponse mockResponse = DeleteObjectResponse.builder()
                .deleteMarker(true)
                .build();

        when(client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(mockResponse);

        // Act
        Boolean result = service.deleteObject(BUCKET, key, null);

        // Assert
        assertThat(result).isTrue();
        verify(client).deleteObject(argThat((DeleteObjectRequest req) ->
                req.bucket().equals(BUCKET) &&
                        req.key().equals(key) &&
                        req.versionId() == null
        ));
    }

    @Test
    void shouldDeleteSpecificObjectVersion() {
        // Arrange
        String key = "document.pdf";
        String versionId = "v1-alpha";
        DeleteObjectResponse mockResponse = DeleteObjectResponse.builder()
                .deleteMarker(false)
                .build();

        when(client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(mockResponse);

        // Act
        Boolean result = service.deleteObject(BUCKET, key, versionId);

        // Assert
        assertThat(result).isFalse();
        verify(client).deleteObject(argThat((DeleteObjectRequest req) ->
                req.versionId().equals(versionId)
        ));
    }

    @Test
    void shouldPropagateExceptionWhenS3FailsToDelete() {
        // Arrange
        when(client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("Access Denied").build());

        // Act & Assert
        assertThrows(S3Exception.class, () ->
                service.deleteObject(BUCKET, "key", null)
        );
    }

    @Test
    void shouldDeleteMultipleObjectsSuccessfully() {
        List<String> keysToDelete = List.of("file1.jpg", "file2.pdf", "file3.png");

        when(client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        service.deleteMultiObject(BUCKET, keysToDelete);

        verify(client).deleteObjects(argThat((DeleteObjectsRequest request) -> {
            boolean bucketMatches = request.bucket().equals(BUCKET);
            List<String> capturedKeys = request.delete().objects().stream()
                    .map(ObjectIdentifier::key)
                    .toList();

            return bucketMatches && capturedKeys.containsAll(keysToDelete)
                    && capturedKeys.size() == keysToDelete.size();
        }));
    }

    @Test
    void shouldPropagateExceptionWhenS3ServiceIsDown() {
        when(client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenThrow(S3Exception.builder().message("Service Unavailable").build());

        assertThrows(S3Exception.class, () ->
                service.deleteMultiObject(BUCKET, List.of("key1"))
        );
    }

    @Test
    void shouldGenerateDownloadLinkSuccessfully() throws MalformedURLException {
        String key = "documento.pdf";
        long expiration = 7;
        URL expectedUrl = new URL("http://localhost:4566/images/documento.pdf?token=abc");

        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);

        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);
        when(mockPresignedRequest.url()).thenReturn(expectedUrl);

        URL result = service.generateLinkToDownload(BUCKET, key, expiration);

        assertNotNull(result);
        assertEquals(expectedUrl, result);

        verify(presigner).presignGetObject(argThat((GetObjectPresignRequest req) ->
                req.getObjectRequest().bucket().equals(BUCKET) &&
                        req.getObjectRequest().key().equals(key) &&
                        req.signatureDuration().equals(Duration.ofDays(expiration))
        ));
    }

    @Test
    void shouldThrowExceptionWhenPresignerFails() {
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Signature error"));

        assertThrows(RuntimeException.class, () ->
                service.generateLinkToDownload(BUCKET, "key", 1)
        );
    }

    @Test
    void shouldReturnStreamingResponseWhenFileExists() throws IOException {
        String key = "video.mp4";
        String contentType = "video/mp4";
        byte[] content = "conteudo binario simulado".getBytes();

        ResponseInputStream<GetObjectResponse> s3Stream = new ResponseInputStream<>(
                GetObjectResponse.builder().contentType(contentType).build(),
                new ByteArrayInputStream(content)
        );

        when(client.getObject(any(GetObjectRequest.class))).thenReturn(s3Stream);

        ResponseEntity<StreamingResponseBody> response = service.downloadStream(BUCKET, key);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.parseMediaType(contentType), response.getHeaders().getContentType());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getBody().writeTo(outputStream);

        assertArrayEquals(content, outputStream.toByteArray());
        verify(client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void shouldPropagateExceptionWhenKeyDoesNotExist() {
        when(client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        assertThrows(NoSuchKeyException.class, () ->
                service.downloadStream(BUCKET, "missing-file.png")
        );
    }

    @Test
    void shouldPropagateExceptionWhenBucketDoesNotExist() {
        when(client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchBucketException.builder().message("Bucket not found").build());

        assertThrows(NoSuchBucketException.class, () ->
                service.downloadStream(BUCKET, "missing-file.png")
        );
    }

    @Test
    void shouldGeneratePreSignedUploadUrlSuccessfully() throws MalformedURLException {
        String key = "uploads/perfil.jpg";
        String contentType = "image/jpeg";
        long expiration = 15;
        URL expectedUrl = new URL("https://s3.amazonaws.com/bucket/key?signature=123");

        PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
        when(presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);
        when(mockPresignedRequest.url()).thenReturn(expectedUrl);

        String result = service.generatePreSignedUploadUrl(BUCKET, key, contentType, expiration);

        assertNotNull(result);
        assertEquals(expectedUrl.toString(), result);

        verify(presigner).presignPutObject(argThat((PutObjectPresignRequest req) ->
                req.putObjectRequest().bucket().equals(BUCKET) &&
                        req.putObjectRequest().key().equals(key) &&
                        req.putObjectRequest().contentType().equals(contentType) &&
                        req.signatureDuration().equals(Duration.ofMinutes(expiration))
        ));
    }

    @Test
    void shouldGeneratePreSignedUploadUrlWhenKeyDoesNotExist() {
        String key = "uploads/perfil.jpg";
        String contentType = "image/jpeg";
        long expiration = 15;

        when(presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        assertThrows(NoSuchKeyException.class, () ->
                service.generatePreSignedUploadUrl(BUCKET, key, contentType, expiration)
        );
    }

    @Test
    void shouldGeneratePreSignedUploadUrlWhenBucketDoesNotExist() {
        String key = "uploads/perfil.jpg";
        String contentType = "image/jpeg";
        long expiration = 15;

        when(presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(NoSuchBucketException.builder().message("Bucket not found").build());

        assertThrows(NoSuchBucketException.class, () ->
                service.generatePreSignedUploadUrl(BUCKET, key, contentType, expiration)
        );
    }

    @Test
    void shouldReturnHeadObject() {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build();
        HeadObjectResponse response = HeadObjectResponse.builder()
                .contentType(CONTENT_TYPE)
                .versionId("1")
                .build();

        when(client.headObject(request)).thenReturn(response);

        HeadObjectResponse objectResponse = this.service.getMetadata(BUCKET, KEY);

        assertThat(objectResponse.contentType()).isEqualTo(response.contentType());

        verifyNoMoreInteractions(client);
        verify(client, times(1)).headObject(request);
    }

    @Test
    void shouldHeadObjectWhenKeyDoesNotExist() {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build();

        when(client.headObject(request))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        assertThrows(NoSuchKeyException.class, () ->
                service.getMetadata(BUCKET, KEY)
        );
    }

    @Test
    void shouldHeadObjectWhenBucketDoesNotExist() {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(BUCKET).key(KEY).build();

        when(client.headObject(request))
                .thenThrow(NoSuchBucketException.builder().message("Bucket not found").build());

        assertThrows(NoSuchBucketException.class, () ->
                service.getMetadata(BUCKET, KEY)
        );
    }

    @Test
    void shouldMoveObjectSuccessfully() {
        String sourceBucket = "temp-bucket";
        String sourceKey = "temp/photo.jpg";
        String destBucket = "prod-bucket";
        String destKey = "posts/photo.jpg";

        when(client.copyObject(any(CopyObjectRequest.class))).thenReturn(CopyObjectResponse.builder().build());
        when(client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());

        service.moveObject(sourceBucket, sourceKey, destBucket, destKey);

        verify(client).copyObject(argThat((CopyObjectRequest req) ->
                req.sourceBucket().equals(sourceBucket) &&
                        req.sourceKey().equals(sourceKey) &&
                        req.destinationBucket().equals(destBucket) &&
                        req.destinationKey().equals(destKey)
        ));

        verify(client).deleteObject(argThat((DeleteObjectRequest req) ->
                req.bucket().equals(sourceBucket) &&
                        req.key().equals(sourceKey)
        ));
    }

    @Test
    void shouldNotDeleteSourceIfCopyFails() {
        when(client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("Copy failed").build());

        assertThrows(S3Exception.class, () ->
                service.moveObject("src", "key", "dest", "key")
        );

        verify(client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void shouldReturnTrueWhenObjectExists() {
        when(client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        boolean result = service.exists(BUCKET, "existing-file.txt");

        assertTrue(result);
        verify(client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void shouldReturnFalseWhenObjectDoesNotExist() {
        when(client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());

        boolean result = service.exists(BUCKET, "missing-file.txt");

        assertFalse(result);
    }

    @Test
    void shouldPropagateExceptionOnServiceError() {
        when(client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("Internal Error").statusCode(500).build());

        assertThrows(S3Exception.class, () -> service.exists(BUCKET, "any-key"));
    }

}
