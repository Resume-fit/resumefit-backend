package com.resumefit.resumefit_backend.domain.review.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 유형", enumAsRef = true)
public enum ReviewType {
    @Schema(description = "결과가 마음에 들어요")
    LIKE,

    @Schema(description = "제 이력서와 잘 맞지 않아요")
    RESUME_MISMATCH,

    @Schema(description = "제 분야/직무와 맞지 않아요")
    FIELD_MISMATCH,

    @Schema(description = "추천 기준이 이해되지 않아요")
    CRITERIA_UNCLEAR,

    @Schema(description = "기타 (직접 입력)")
    OTHER
}