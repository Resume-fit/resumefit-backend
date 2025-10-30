package com.resumefit.resumefit_backend.domain.resume.controller;

import com.resumefit.resumefit_backend.domain.resume.dto.ResumeDetailDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.service.ResumeService;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import java.util.List;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<Void> saveResume(
        @RequestBody ResumePostDto resumePostDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.processResumePost(resumePostDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    ResponseEntity<List<ResumeSummaryDto>> getAllMyResume(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getAllMyResume(userDetails));
    }

    @GetMapping("/{resumeId}") // 예시: GET /api/resumes/{resumeId}
    public ResponseEntity<ResumeDetailDto> getResumeById(
        @PathVariable("resumeId") Long resumeId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getResume(resumeId, userDetails));
    }

    @DeleteMapping("/{resumeId}")
    ResponseEntity<Void> deleteResume(@AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("resumeId") Long resumeId) {
        resumeService.deleteResume(resumeId, userDetails);
        return ResponseEntity.ok().build();
    }


}
