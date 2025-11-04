package com.resumefit.resumefit_backend.domain.jobposition.entity.matching;

import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;

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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matching")
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추천
    @JoinColumn(name = "resume_id", nullable = false) // 외래 키 컬럼 지정
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id", nullable = false) // 외래 키 컬럼 지정
    private JobPosition jobPosition;

    @Enumerated(EnumType.STRING) // DB에 Enum 이름을 문자열로 저장 (예: "SUITABLE")
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;
}
