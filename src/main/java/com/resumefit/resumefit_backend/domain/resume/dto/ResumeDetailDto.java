package com.resumefit.resumefit_backend.domain.resume.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이력서 상세 정보 DTO")
public class ResumeDetailDto {

    @Schema(description = "이력서 제목", example = "2024 상반기 백엔드 개발자 이력서")
    private String title;

    @Schema(description = "S3 파일 키", example = "uuid_홍길동_이력서.pdf")
    private String fileKey;

    @Schema(description = "생성 일시", example = "2024-06-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2024-06-20T14:20:00")
    private LocalDateTime updatedAt;

    @Schema(
            description = "PDF 열람용 Pre-signed URL (5분간 유효)",
            example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uuid_resume.pdf?X-Amz-...")
    private String pdfViewUrl;
}
