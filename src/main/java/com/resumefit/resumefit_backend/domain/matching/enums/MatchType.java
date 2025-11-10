package com.resumefit.resumefit_backend.domain.matching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchType {
    SUITABLE("적합"), // 현재 역량으로 지원 가능
    GROWTH_TRACK("성장 트랙"); // 일부 역량 보충 필요

    private final String description;
}
