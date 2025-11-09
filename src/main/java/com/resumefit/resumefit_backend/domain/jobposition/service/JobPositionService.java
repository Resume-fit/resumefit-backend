package com.resumefit.resumefit_backend.domain.jobposition.service;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionDetailDto;
import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import com.resumefit.resumefit_backend.domain.jobposition.mapper.JobPositionMapper;
import com.resumefit.resumefit_backend.domain.jobposition.repository.JobPositionRepository;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;
    private final JobPositionMapper jobPositionMapper;

    public List<JobPositionSummaryDto> getAllJobPositions() {
        return jobPositionMapper.toJobPositionSummaryDtoList(jobPositionRepository.findAll());
    }

    public List<JobPositionSummaryDto> getAllJobPositionsByCategory(String category) {
        List<JobPosition> jobPositions = jobPositionRepository.findByJobCategory(category);
        return jobPositionMapper.toJobPositionSummaryDtoList(jobPositions);
    }

    public JobPositionDetailDto getJobPosition(Long id) {
        JobPosition jobPosition =
                jobPositionRepository
                        .findById(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.JOB_POSITION_NOT_FOUND));
        return jobPositionMapper.toJobPositionDetailDto(jobPosition);
    }
}
