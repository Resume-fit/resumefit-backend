package com.resumefit.resumefit_backend.domain.jobposition.service;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import com.resumefit.resumefit_backend.domain.jobposition.mapper.JobPositionMapper;
import com.resumefit.resumefit_backend.domain.jobposition.repository.JobPositionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
