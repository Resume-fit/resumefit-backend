package com.resumefit.resumefit_backend.domain.resume.mapper;

import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    // Resume toResumeEntity(ResumePostDto resumePostDto);
    ResumeSummaryDto toResumeSummaryDto(Resume resume);

    List<ResumeSummaryDto> toResumeSummaryDtoList(List<Resume> resumeList);
}
