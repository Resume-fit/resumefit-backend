package com.resumefit.resumefit_backend.domain.jobposition.dto;

import lombok.Data;

@Data
public class JobPositionSummaryDto {

    private Long id;
    private String positionName;
    private String companyName;
    private String workPlace;
    private String employmentType;
    private String url;

}
