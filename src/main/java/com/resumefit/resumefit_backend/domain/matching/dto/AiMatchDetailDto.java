package com.resumefit.resumefit_backend.domain.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resumefit.resumefit_backend.domain.matching.enums.MatchType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMatchDetailDto {

    @JsonProperty("MATCH_TYPE")
    private MatchType matchType;

    @JsonProperty("JobPositionId")
    private Long jobPositionId;

    @JsonProperty("COMMENT")
    private String comment;
}
