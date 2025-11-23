package com.resumefit.resumefit_backend.domain.matching.enums;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "매칭 유형", enumAsRef = true)
public enum MatchType {
    @Schema(description = "현재 역량으로 지원 가능")
    SUITABLE("적합"),

    @Schema(description = "일부 역량 보충 필요")
    GROWTH_TRACK("성장 트랙");

    private final String description;
}
