package com.resumefit.resumefit_backend.domain.jobposition.repository;

import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findByJobCategory(String jobCategory);
}
