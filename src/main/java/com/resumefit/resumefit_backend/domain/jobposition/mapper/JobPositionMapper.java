package com.resumefit.resumefit_backend.domain.jobposition.mapper;


import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobPositionMapper {

    @Mapping(source = "company.companyName", target = "companyName")
    JobPositionSummaryDto toJobPositionSummaryDto(JobPosition jobPosition);

    List<JobPositionSummaryDto> toJobPositionSummaryDtoList(List<JobPosition> jobPositions);

}
