package com.resumefit.resumefit_backend.domain.review.entity;

import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.review.enums.ReviewType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType reviewType;

    @CreationTimestamp // 피드백 제출 시간
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT") // 매칭된 공고 ID 직렬화
    private String recommendedJobPositionIds;
}
