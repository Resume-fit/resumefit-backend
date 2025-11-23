package com.resumefit.resumefit_backend.domain.resume.dto;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "이력서-채용공고 매칭 결과 DTO")
public class MatchingResponseDto {

    @Schema(description = "매칭된 채용공고 정보")
    private JobPositionSummaryDto jobPosition;

    @Schema(
        description = "매칭 유형 (SUITABLE: 적합, GROWTH_TRACK: 성장 트랙)",
        example = "SUITABLE",
        allowableValues = {"SUITABLE", "GROWTH_TRACK"}
    )
    private String matchType;

    @Schema(
        description = "AI 매칭 코멘트 (매칭 이유 설명)",
        example = "Spring Boot 3년 경력과 JPA 활용 능력이 해당 포지션의 요구사항과 잘 부합합니다."
    )
    private String comment;
}