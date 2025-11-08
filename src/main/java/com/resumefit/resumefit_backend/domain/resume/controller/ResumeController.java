package com.resumefit.resumefit_backend.domain.resume.controller;

import com.resumefit.resumefit_backend.domain.resume.dto.ResumeDetailDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.service.ResumeService;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "이력서 저장", description = "사용자의 이력서를 저장합니다.")
    @PostMapping
    public ResponseEntity<Void> saveResume(
            @RequestBody ResumePostDto resumePostDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.processResumePost(resumePostDto, userDetails);
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
}
