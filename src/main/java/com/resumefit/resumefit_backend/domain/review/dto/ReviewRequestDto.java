package com.resumefit.resumefit_backend.domain.review.dto;

import com.resumefit.resumefit_backend.domain.matching.enums.ReviewType;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;

@Data
public class ReviewRequestDto {

    @NotNull private ReviewType reviewType;

    private List<Long> recommendedJobPositionIds;
}
