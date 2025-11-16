package com.resumefit.resumefit_backend.domain.matching.controller;

import com.resumefit.resumefit_backend.domain.matching.service.MatchingService;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = " 매칭 api", description = "매칭 결과와 관련된 API들입니다.")
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "이력서의 매칭결과 조회", description = "이력서 ID를 통해 매칭된 결과들을 조회합니다.")
    @GetMapping("/{resumeId}")
    ResponseEntity<List<MatchingResponseDto>> getMatching(
        @PathVariable Long resumeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(matchingService.getMatching(resumeId, userDetails));
    }

}
