package com.resumefit.resumefit_backend.domain.review.controller;

import com.resumefit.resumefit_backend.domain.review.dto.ReviewRequestDto;
import com.resumefit.resumefit_backend.domain.review.service.ReviewService;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 제출", description = "이력서 매칭 결과에 대한 리뷰를 제출합니다.")
    @PostMapping("/{resumeId}")
    public ResponseEntity<String> submitReview(
            @PathVariable Long resumeId,
            @RequestBody ReviewRequestDto reviewDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        reviewService.submitReview(resumeId, reviewDto, userDetails);
        return ResponseEntity.ok("리뷰가 성공적으로 제출되었습니다.");
    }

    @Operation(summary = "리뷰 존재 여부 확인", description = "특정 이력서에 대한 리뷰가 이미 존재하는지 확인합니다.")
    @GetMapping("/check/{resumeId}")
    public ResponseEntity<Boolean> checkReviewExists(
            @PathVariable Long resumeId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean exists = reviewService.checkReviewExists(resumeId, userDetails);
        return ResponseEntity.ok(exists);
    }
}
