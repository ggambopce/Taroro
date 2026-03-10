package com.neocompany.taroro.domain.image;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neocompany.taroro.domain.image.dto.S3UploadResult;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ImageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3ImageService(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.region}") String region
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.region = region;
    }

    public S3UploadResult upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("files는 비어있을 수 없다.");
        }

        List<S3UploadResult.Item> items = new ArrayList<>();

        for (MultipartFile f : files) {
            validateImageFile(f);

            // 원본 파일명
            String original = Optional.ofNullable(f.getOriginalFilename()).orElse("file");
            String safeName = sanitizeFileName(original);

            // 파일 경로
            String key = String.format("%s/%s_%s", UUID.randomUUID(), safeName);

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(Optional.ofNullable(f.getContentType()).orElse("application/octet-stream"))
                    .build();

            try {
                // 메모리에 올린후 바이트로 업로드하는 단순한 방식
                s3Client.putObject(putReq, RequestBody.fromBytes(f.getBytes()));
            } catch (Exception e) {
                throw new RuntimeException("S3 업로드 실패: " + original, e);
            }

            items.add(new S3UploadResult.Item(original, key, publicUrl(key)));
        }

        return new S3UploadResult(items);
    }

    // 이미지 파일 검증 contentType이 image/*가 아니면 차단
    private void validateImageFile(MultipartFile f) {
        if (f == null || f.isEmpty()) throw new IllegalArgumentException("빈 파일은 업로드할 수 없다.");
        String ct = f.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능하다. contentType=" + ct);
        }
    }

    // 원본 파일명 한글,공백 안전화 처리
    private String sanitizeFileName(String name) {
        // 한글/공백이 있어도 되지만 URL 안전성 위해 최소 처리
        String trimmed = name.trim().replace("\\", "_").replace("/", "_");
        return URLEncoder.encode(trimmed, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String publicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
