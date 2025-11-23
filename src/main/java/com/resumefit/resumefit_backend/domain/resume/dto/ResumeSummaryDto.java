package com.resumefit.resumefit_backend.domain.resume.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이력서 요약 정보 DTO")
public class ResumeSummaryDto {

    @Schema(description = "이력서 ID", example = "1")
    private Long id;

    @Schema(description = "이력서 제목", example = "2024 상반기 백엔드 개발자 이력서")
    private String title;

    @Schema(description = "생성 일시", example = "2024-06-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시", example = "2024-06-20T14:20:00")
    private LocalDateTime updatedAt;
}
