package com.resumefit.resumefit_backend.domain.review.dto;

import com.resumefit.resumefit_backend.domain.review.enums.ReviewType;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;

@Data
@Schema(description = "매칭 결과 피드백 요청 DTO")
public class ReviewRequestDto {

    @Schema(
        description = "피드백 유형",
        example = "LIKE",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private ReviewType reviewType;

    @Schema(description = "추천된 채용공고 ID 목록 (피드백 대상)", example = "[1, 3, 5]")
    private List<Long> recommendedJobPositionIds;
}