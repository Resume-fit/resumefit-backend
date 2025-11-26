package com.resumefit.resumefit_backend.domain.review.repository;

import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.review.entity.Review;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** 특정 이력서의 모든 리뷰 삭제 @Modifying과 @Transactional 필수 */
    @Transactional
    @Modifying
    void deleteByResume(Resume resume);

    boolean existsByResumeId(Long resumeId);

    // Resume로 리뷰 목록 조회
    List<Review> findByResume(Resume resume);

    // Resume로 단일 리뷰 조회 (최신순)
    Optional<Review> findFirstByResumeOrderByIdDesc(Resume resume);
}
