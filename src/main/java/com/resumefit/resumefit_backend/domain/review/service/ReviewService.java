package com.resumefit.resumefit_backend.domain.review.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.resume.repository.ResumeRepository;
import com.resumefit.resumefit_backend.domain.review.dto.ReviewRequestDto;
import com.resumefit.resumefit_backend.domain.review.entity.Review;
import com.resumefit.resumefit_backend.domain.review.enums.ReviewType;
import com.resumefit.resumefit_backend.domain.review.repository.ReviewRepository;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ResumeRepository resumeRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void submitReview(
            Long resumeId, ReviewRequestDto reviewDto, CustomUserDetails userDetails) {

        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // OTHER 타입일 때 otherComment 필수 검증 추가
        if (reviewDto.getReviewType() == ReviewType.OTHER
                && (reviewDto.getOtherComment() == null || reviewDto.getOtherComment().isBlank())) {
            throw new CustomException(ErrorCode.OTHER_COMMENT_REQUIRED);
        }

        String recommendedIdsJson = null;
        if (reviewDto.getRecommendedJobPositionIds() != null
                && !reviewDto.getRecommendedJobPositionIds().isEmpty()) {
            try {
                recommendedIdsJson =
                        objectMapper.writeValueAsString(reviewDto.getRecommendedJobPositionIds());
            } catch (JsonProcessingException e) {
                log.error(
                        "Failed to serialize recommendedJobPositionIds for resumeId: {}", resumeId);
                throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
            }
        }

        // Resume당 하나의 리뷰만 유지: 기존 리뷰가 있으면 업데이트, 없으면 생성
        Optional<Review> existingReview = reviewRepository.findFirstByResumeOrderByIdDesc(resume);

        if (existingReview.isPresent()) {
            // 기존 리뷰 업데이트
            Review review = existingReview.get();
            log.info("기존 리뷰 업데이트 - Resume ID: {}, Review ID: {}", resumeId, review.getId());

            review.setReviewType(reviewDto.getReviewType());
            review.setRecommendedJobPositionIds(recommendedIdsJson);
            review.setOtherComment(reviewDto.getOtherComment());

            reviewRepository.save(review);
            log.info("리뷰 업데이트 완료 - Review Type: {}", reviewDto.getReviewType());
        } else {
            // 새 리뷰 생성
            log.info("새 리뷰 생성 - Resume ID: {}", resumeId);

            Review review =
                    Review.builder()
                            .resume(resume)
                            .reviewType(reviewDto.getReviewType())
                            .recommendedJobPositionIds(recommendedIdsJson)
                            .otherComment(reviewDto.getOtherComment())
                            .build();

            reviewRepository.save(review);
            log.info("리뷰 생성 완료 - Review Type: {}", reviewDto.getReviewType());
        }
    }

    @Transactional(readOnly = true)
    public boolean checkReviewExists(Long resumeId, CustomUserDetails userDetails) {
        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Optional<Review> existingReview = reviewRepository.findFirstByResumeOrderByIdDesc(resume);
        boolean exists = existingReview.isPresent();

        log.info("리뷰 존재 확인 - Resume ID: {}, Exists: {}", resumeId, exists);
        return exists;
    }
}
