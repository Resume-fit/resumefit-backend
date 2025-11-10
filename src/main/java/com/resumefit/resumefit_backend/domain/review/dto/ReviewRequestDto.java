package com.resumefit.resumefit_backend.domain.review.dto;

import com.resumefit.resumefit_backend.domain.matching.enums.ReviewType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class ReviewRequestDto {

    @NotNull
    private ReviewType reviewType;

    private List<Long> recommendedJobPositionIds;

}
