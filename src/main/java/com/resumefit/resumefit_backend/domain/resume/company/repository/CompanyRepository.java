package com.resumefit.resumefit_backend.domain.resume.company.repository;

import com.resumefit.resumefit_backend.domain.resume.company.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}
