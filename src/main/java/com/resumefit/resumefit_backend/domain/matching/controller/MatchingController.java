package com.resumefit.resumefit_backend.domain.matching.controller;

import com.resumefit.resumefit_backend.domain.matching.service.MatchingService;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "매칭", description = "이력서-채용공고 매칭 결과 관련 API")
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(
            summary = "매칭 결과 조회",
            description =
                    """
                    특정 이력서의 AI 매칭 결과를 조회합니다.

                    **반환 정보:**
                    - 매칭된 채용공고 목록
                    - 매칭 유형 (SUITABLE / GROWTH_TRACK)
                    - AI 코멘트 (매칭 이유 설명)

                    **매칭 유형 설명:**
                    - SUITABLE: 현재 역량으로 지원 가능한 공고
                    - GROWTH_TRACK: 일부 역량 보충이 필요한 공고

                    **권한:**
                    - 본인의 이력서 매칭 결과만 조회 가능

                    **참고:**
                    - 매칭이 수행되지 않은 이력서는 빈 배열을 반환합니다.
                    - 매칭을 수행하려면 `POST /api/resumes/{resumeId}/match` API를 호출하세요.
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content =
                        @Content(
                                array =
                                        @ArraySchema(
                                                schema =
                                                        @Schema(
                                                                implementation =
                                                                        MatchingResponseDto
                                                                                .class)))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 또는 권한 없음"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
    })
    @GetMapping("/{resumeId}")
    public ResponseEntity<List<MatchingResponseDto>> getMatching(
            @Parameter(description = "이력서 ID", required = true, example = "1") @PathVariable
                    Long resumeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(matchingService.getMatching(resumeId, userDetails));
    }
}
