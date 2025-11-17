package com.resumefit.resumefit_backend.domain.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    @JsonProperty("SUITABLE")
    private Map<String, AiMatchDetailDto> suitable;

    @JsonProperty("GROWTH_TRACK")
    private Map<String, AiMatchDetailDto> growthTrack;
}
