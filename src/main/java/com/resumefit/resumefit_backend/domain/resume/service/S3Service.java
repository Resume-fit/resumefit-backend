package com.resumefit.resumefit_backend.domain.resume.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client; // ✅ S3Client 주입
    private final S3Presigner s3Presigner;

    // application.yml 에 설정된 버킷 이름 주입
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // application.yml 에 설정된 리전 주입 (URL 생성 시 필요)
    @Value("${cloud.aws.region.static}")
    private String region;

    public String generatePresignedUrl(String fileKey, Duration duration) {
        if (fileKey == null || fileKey.isBlank()) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration) // 외부에서 전달받은 유효 시간 설정
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            return null; // 실패 시 null 반환 또는 예외 던지기
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }

        String originalFileName = file.getOriginalFilename();
        String fileName = generateFileName(originalFileName);

        // PutObjectRequest 생성 (SDK v2 방식)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType()) // 파일의 Content Type 설정
            // .acl(ObjectCannedACL.PUBLIC_READ)
            .build();

        // RequestBody 생성 (InputStream 사용)
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest, requestBody);

        return fileName; // S3에 저장된 파일 이름 반환
    }

    public String uploadPdfBytes(byte[] fileBytes, String desiredFileName) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File bytes are null or empty");
        }
        if (desiredFileName == null || desiredFileName.isBlank()) {
            desiredFileName = "generated_resume.pdf"; // 기본 파일명 설정
        }

        String fileKey = generateFileName(desiredFileName); // 고유 키 생성

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentType("application/pdf") // Content Type을 PDF로 명시
            .contentLength((long) fileBytes.length) // Content Length 설정
            // .acl(ObjectCannedACL.PUBLIC_READ) // 동일하게 공개 읽기 권한 설정
            .build();

        // RequestBody 생성 (byte 배열 사용)
        RequestBody requestBody = RequestBody.fromBytes(fileBytes);

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest, requestBody);

        return fileKey; // S3에 저장된 최종 키 반환
    }
    public String getFileUrl(String fileName) {
        // AWS SDK v2는 getUrl()을 직접 제공하지 않습니다. URL 형식을 직접 구성합니다.
        // 형식: https://{bucketName}.s3.{region}.amazonaws.com/{fileName}
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    private String generateFileName(String originalFileName) {
        if (originalFileName == null) {
            originalFileName = "file"; // 원본 이름이 없는 경우 기본값 설정
        }
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    public void deleteFile(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            throw new IllegalArgumentException("File key is null or empty");
        }

        try {
            s3Client.deleteObject(builder -> builder
                .bucket(bucketName)
                .key(fileKey)
                .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public byte[] downloadFileAsBytes(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

            // getObjectAsBytes: 파일을 바이트 배열로 직접 다운로드
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (Exception e) {
            log.error("S3에서 파일 다운로드 실패. Key: {}, Error: {}", fileKey, e.getMessage(), e);
            return null; // 또는 예외 발생
        }
    }
}