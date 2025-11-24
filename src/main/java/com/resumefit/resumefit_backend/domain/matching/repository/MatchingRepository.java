package com.resumefit.resumefit_backend.domain.matching.repository;

import com.resumefit.resumefit_backend.domain.matching.entity.Matching;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    /**
     * 특정 이력서의 모든 매칭 결과 삭제
     * @Modifying과 @Transactional 필수
     */
    @Transactional
    @Modifying
    void deleteByResume(Resume resume);

    /**
     * 특정 이력서의 모든 매칭 결과 조회
     */
    List<Matching> findAllByResume(Resume resume);
}