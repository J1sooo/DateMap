package com.est.back.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageUploadService {
    private final S3Client s3Client;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // S3 파일 URL 반환
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }

    private static final Set<String> EXCLUDED_KEY = Set.of(
            "noImage.jpg",
            "default_profile.png"
    );

    public void deleteFile(String imgUrl) {
        String key = extractKeyFromUrl(imgUrl);

        if (key == null) return;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String extractKeyFromUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.isBlank()) return null;

        URI uri = URI.create(imgUrl);
        String path = uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();

        return EXCLUDED_KEY.contains(path) ? null : path;
    }
}
