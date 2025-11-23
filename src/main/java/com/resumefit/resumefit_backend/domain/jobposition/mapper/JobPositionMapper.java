package com.resumefit.resumefit_backend.domain.jobposition.mapper;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionDetailDto;
import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import com.resumefit.resumefit_backend.domain.jobposition.entity.Requirement;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobPositionMapper {

    @Mapping(source = "company.companyName", target = "companyName")
    JobPositionSummaryDto toJobPositionSummaryDto(JobPosition jobPosition);

    List<JobPositionSummaryDto> toJobPositionSummaryDtoList(List<JobPosition> jobPositions);

    @Mapping(source = "company.companyName", target = "companyName")
    JobPositionDetailDto toJobPositionDetailDto(JobPosition jobPosition);

    JobPositionDetailDto.RequirementDto requirementToRequirementDto(Requirement requirement);
}
