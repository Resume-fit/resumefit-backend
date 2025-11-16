package com.resumefit.resumefit_backend.domain.matching.mapper;

import com.resumefit.resumefit_backend.domain.matching.entity.Matching;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchingMapper {

    @Mapping(source = "jobPosition", target = "jobPosition")
    MatchingResponseDto toMatchingResponseDto(Matching matching);

    List<MatchingResponseDto> toMatchingResponseDtoList(List<Matching> matchingList);
}
