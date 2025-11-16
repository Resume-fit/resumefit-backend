package com.resumefit.resumefit_backend.domain.resume.controller;

import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeDetailDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.service.ResumeService;
import com.resumefit.resumefit_backend.domain.review.dto.ReviewRequestDto;
import com.resumefit.resumefit_backend.domain.review.service.ReviewService;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = " 이력서 api", description = "이력서와 관련된 API들입니다.")
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ReviewService reviewService;
    private final RestClient fastApiRestClient;

    @Operation(summary = "이력서 작성 및 저장", description = "사용자가 이력서를 작성 및 저장합니다.")
    @PostMapping
    public ResponseEntity<Void> saveResume(
            @RequestBody ResumePostDto resumePostDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.processResumePost(resumePostDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이력서 파일 업로드", description = "PDF 형식의 이력서를 업로드합니다.")
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadResumeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.uploadResumeFile(file, title, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 이력서 조회", description = "사용자의 모든 이력서를 조회합니다.")
    @GetMapping
    ResponseEntity<List<ResumeSummaryDto>> getAllMyResume(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getAllMyResume(userDetails));
    }

    @Operation(summary = "이력서 상세 조회", description = "이력서 ID를 통해 특정 이력서를 조회합니다.")
    @GetMapping("/{resumeId}") // 예시: GET /api/resumes/{resumeId}
    public ResponseEntity<ResumeDetailDto> getResumeById(
            @PathVariable("resumeId") Long resumeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getResume(resumeId, userDetails));
    }

    @Operation(summary = "이력서 삭제", description = "이력서 ID를 통해 특정 이력서를 삭제합니다.")
    @DeleteMapping("/{resumeId}")
    ResponseEntity<Void> deleteResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("resumeId") Long resumeId) {
        resumeService.deleteResume(resumeId, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이력서 후기 제출", description = "특정 매칭에 대한 후기를 제출합니다.")
    @DeleteMapping("/{resumeId}/review")
    ResponseEntity<Void> submitReview(
            @PathVariable("resumeId") Long resumeId,
            @Valid @RequestBody ReviewRequestDto reviewRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.submitReview(resumeId, reviewRequestDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이력서 매칭", description = "특정 이력서를 채용공고들과 매칭합니다.")
    @PostMapping("/{resumeId}/match")
    ResponseEntity<List<MatchingResponseDto>> matchResume(
        @PathVariable("resumeId") Long resumeId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MatchingResponseDto> matchingResponseDtoList = resumeService.matchResume(resumeId, userDetails);
        return ResponseEntity.ok(matchingResponseDtoList);
    }

    @Operation(summary = "FastAPI 서버 헬스 체크", description = "FastAPI 서버의 루트 엔드포인트를 호출하여 응답을 확인합니다.")
    @GetMapping("/health-check")
    public ResponseEntity<Map<String, String>> checkFastApi() {
        Map<String, String> healthStatus = resumeService.checkFastApiHealth();

        if ("UP".equals(healthStatus.get("status"))) {
            // 성공 시 200 OK
            return ResponseEntity.ok(healthStatus);
        } else {
            // 실패 시 503 Service Unavailable
            return ResponseEntity.status(503).body(healthStatus);
        }
    }

}
