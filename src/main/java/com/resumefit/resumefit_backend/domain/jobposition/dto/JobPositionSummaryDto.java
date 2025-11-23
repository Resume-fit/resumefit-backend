package com.resumefit.resumefit_backend.domain.jobposition.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "채용공고 요약 정보 DTO")
public class JobPositionSummaryDto {

    @Schema(description = "채용공고 ID", example = "1")
    private Long id;

    @Schema(description = "포지션명", example = "백엔드 개발자")
    private String positionName;

    @Schema(description = "회사명", example = "(주)테크컴퍼니")
    private String companyName;

    @Schema(description = "근무지", example = "서울시 강남구")
    private String workPlace;

    @Schema(description = "고용 형태", example = "정규직")
    private String employmentType;

    @Schema(description = "원본 채용공고 URL", example = "https://careers.company.com/job/123")
    private String url;
}