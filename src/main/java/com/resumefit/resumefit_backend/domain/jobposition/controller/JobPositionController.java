package com.resumefit.resumefit_backend.domain.jobposition.controller;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionDetailDto;
import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.service.JobPositionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "채용공고", description = "채용공고 조회 관련 API")
@RequestMapping("/api/job-positions")
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @Operation(
            summary = "전체 채용공고 조회",
            description =
                    """
                    등록된 모든 채용공고 목록을 조회합니다.

                    **반환 정보:**
                    - 공고 ID, 포지션명, 회사명
                    - 근무지, 고용형태
                    - 원본 공고 URL
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
                                                                        JobPositionSummaryDto
                                                                                .class))))
    })
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<List<JobPositionSummaryDto>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionService.getAllJobPositions());
    }

    @Operation(
            summary = "직무별 채용공고 조회",
            description =
                    """
                    특정 직무 카테고리의 채용공고 목록을 조회합니다.

                    **직무 카테고리 예시:**
                    - 백엔드, 프론트엔드, 풀스택
                    - 데이터 엔지니어, AI/ML
                    - DevOps, 인프라
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
                                                                        JobPositionSummaryDto
                                                                                .class))))
    })
    @SecurityRequirements
    @GetMapping("/category/{category}")
    public ResponseEntity<List<JobPositionSummaryDto>> getAllJobPositionsByCategory(
            @Parameter(
                            description = "직무 카테고리",
                            required = true,
                            examples = {
                                @ExampleObject(name = "백엔드", value = "백엔드"),
                                @ExampleObject(name = "프론트엔드", value = "프론트엔드"),
                                @ExampleObject(name = "풀스택", value = "풀스택"),
                                @ExampleObject(name = "데이터", value = "데이터 엔지니어")
                            })
                    @PathVariable("category")
                    String category) {
        return ResponseEntity.ok(jobPositionService.getAllJobPositionsByCategory(category));
    }

    @Operation(
            summary = "채용공고 상세 조회",
            description =
                    """
                    특정 채용공고의 상세 정보를 조회합니다.

                    **반환 정보:**
                    - 기본 정보 (포지션명, 회사명, 근무지, 고용형태)
                    - 직무 카테고리
                    - 주요 업무 내용
                    - 자격 요건 (REQUIRED: 필수, PREFERRED: 우대)
                    - 원본 공고 URL
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = JobPositionDetailDto.class))),
        @ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionDetailDto> getJobPosition(
            @Parameter(description = "채용공고 ID", required = true, example = "1") @PathVariable("id")
                    Long id) {
        return ResponseEntity.ok(jobPositionService.getJobPosition(id));
    }
}
