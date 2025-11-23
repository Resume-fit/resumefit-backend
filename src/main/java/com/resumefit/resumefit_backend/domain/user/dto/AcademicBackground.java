package com.resumefit.resumefit_backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

@Getter
@Schema(description = "최종 학력", enumAsRef = true)
public enum AcademicBackground {

    @Schema(description = "고등학교 졸업")
    HIGH_SCHOOL("고등학교 졸업"),

    @Schema(description = "대학 졸업 (2, 3년제)")
    ASSOCIATE_DEGREE("대학 졸업 (2, 3년)"),

    @Schema(description = "대학교 졸업 (4년제)")
    BACHELOR_DEGREE("대학교 졸업 (4년)"),

    @Schema(description = "석사 졸업")
    MASTER_DEGREE("석사 졸업"),

    @Schema(description = "박사 졸업")
    DOCTORATE_DEGREE("박사 졸업");

    private final String description;

    AcademicBackground(String description) {
        this.description = description;
    }
}