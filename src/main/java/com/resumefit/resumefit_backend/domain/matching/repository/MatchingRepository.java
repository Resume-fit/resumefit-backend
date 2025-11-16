package com.resumefit.resumefit_backend.domain.matching.repository;

import com.resumefit.resumefit_backend.domain.matching.entity.Matching;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    void deleteByResume(Resume resume);

    List<Matching> findAllByResume(Resume resume);
}
