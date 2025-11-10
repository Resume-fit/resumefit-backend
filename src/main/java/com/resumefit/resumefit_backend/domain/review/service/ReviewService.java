package com.resumefit.resumefit_backend.domain.review.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.resume.repository.ResumeRepository;
import com.resumefit.resumefit_backend.domain.review.dto.ReviewRequestDto;
import com.resumefit.resumefit_backend.domain.review.entity.Review;
import com.resumefit.resumefit_backend.domain.review.repository.ReviewRepository;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ResumeRepository resumeRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void submitReview(Long resumeId, ReviewRequestDto reviewDto, CustomUserDetails userDetails) {

        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String recommendedIdsJson = null;
        if (reviewDto.getRecommendedJobPositionIds() != null && !reviewDto.getRecommendedJobPositionIds().isEmpty()) {
            try {
                recommendedIdsJson = objectMapper.writeValueAsString(reviewDto.getRecommendedJobPositionIds());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize recommendedJobPositionIds for resumeId: {}", resumeId);
                throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
            }
        }

        Review review = Review.builder()
            .resume(resume)
            .reviewType(reviewDto.getReviewType())
            .recommendedJobPositionIds(recommendedIdsJson)
            .build();

        reviewRepository.save(review);
    }
}
