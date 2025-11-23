package com.resumefit.resumefit_backend.domain.jobposition.entity;

import com.resumefit.resumefit_backend.domain.company.entity.Company;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(
        name = "job_position",
        indexes = {@Index(name = "idx_job_category", columnList = "jobCategory")})
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String positionName;

    @Column(name = "job_category")
    private String jobCategory;

    private String workPlace;

    private String employmentType;

    @Column(columnDefinition = "TEXT")
    private String mainJob;

    @OneToMany(mappedBy = "jobPosition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Requirement> requirements = new ArrayList<>();

    private String url;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
