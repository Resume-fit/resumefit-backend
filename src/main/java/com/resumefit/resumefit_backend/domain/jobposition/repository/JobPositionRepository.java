package com.resumefit.resumefit_backend.domain.jobposition.repository;

import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findByJobCategory(String jobCategory);

}
