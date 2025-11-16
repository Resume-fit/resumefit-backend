package com.resumefit.resumefit_backend.domain.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    @JsonProperty("SUITABLE")
    private Map<String, AiMatchDetailDto> suitable;

    @JsonProperty("GROWTH_TRACK")
    private Map<String, AiMatchDetailDto> growthTrack;
}
