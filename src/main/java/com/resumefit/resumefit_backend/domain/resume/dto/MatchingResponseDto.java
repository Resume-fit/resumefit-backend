package com.resumefit.resumefit_backend.domain.resume.dto;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDto {

    private JobPositionSummaryDto jobPosition;
    private String matchType;
    private String comment;
}
