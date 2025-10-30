package com.resumefit.resumefit_backend.domain.resume.repository;

import com.resumefit.resumefit_backend.domain.resume.entity.Resume;

import com.resumefit.resumefit_backend.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);
}
