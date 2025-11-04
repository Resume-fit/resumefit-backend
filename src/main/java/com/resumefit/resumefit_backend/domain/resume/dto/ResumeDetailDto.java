package com.resumefit.resumefit_backend.domain.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeDetailDto {

    private String title;
    private String fileKey; // S3에 저장된 파일의 고유 키
    private LocalDateTime createdAt; // 생성 일시
    private LocalDateTime updatedAt; // 최종 수정 일시
    private String pdfViewUrl;
}
