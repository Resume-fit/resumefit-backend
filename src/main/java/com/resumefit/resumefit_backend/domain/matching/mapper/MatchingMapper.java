package com.resumefit.resumefit_backend.domain.matching.mapper;

import com.resumefit.resumefit_backend.domain.jobposition.mapper.JobPositionMapper;
import com.resumefit.resumefit_backend.domain.matching.entity.Matching;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = JobPositionMapper.class)
public interface MatchingMapper {

    MatchingResponseDto toMatchingResponseDto(Matching matching);

    List<MatchingResponseDto> toMatchingResponseDtoList(List<Matching> matchingList);
}