package com.resumefit.resumefit_backend.domain.jobposition.dto;

import com.resumefit.resumefit_backend.domain.skill.enums.SkillType;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "채용공고 상세 정보 DTO")
public class JobPositionDetailDto {

    @Schema(description = "포지션명", example = "백엔드 개발자")
    private String positionName;

    @Schema(description = "회사명", example = "(주)테크컴퍼니")
    private String companyName;

    @Schema(description = "직무 카테고리", example = "백엔드")
    private String jobCategory;

    @Schema(description = "근무지", example = "서울시 강남구 테헤란로")
    private String workPlace;

    @Schema(description = "고용 형태", example = "정규직")
    private String employmentType;

    @Schema(description = "원본 채용공고 URL", example = "https://careers.company.com/job/123")
    private String url;

    @Schema(
            description = "주요 업무 내용",
            example = "- 결제 시스템 백엔드 개발 및 운영\n- API 설계 및 구현\n- 데이터베이스 설계 및 최적화")
    private String mainJob;

    @Schema(description = "자격 요건 및 우대 사항 목록")
    private List<RequirementDto> requirements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자격 요건/우대 사항 정보")
    public static class RequirementDto {

        @Schema(description = "요건 유형 (REQUIRED: 필수, PREFERRED: 우대)", example = "REQUIRED")
        private SkillType type;

        @Schema(description = "요건 내용", example = "Spring Boot 2년 이상 경험자")
        private String content;
    }
}
