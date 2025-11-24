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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "이력서", description = "이력서 작성, 조회, 삭제, 매칭 관련 API")
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ReviewService reviewService;
    private final RestClient fastApiRestClient;

    @Operation(
            summary = "이력서 작성",
            description =
                    """
                    새로운 이력서를 작성하고 저장합니다.

                    **입력 가능한 정보:**
                    - 이력서 제목, 자기소개
                    - 학력 사항 (학교명, 전공, 학위, 재학상태, 학점)
                    - 경력 사항 (회사명, 직무, 기간, 업무내용)
                    - 기술 스택
                    - 자격증
                    - 프로젝트 경험
                    - 수상/활동 내역

                    **처리 과정:**
                    1. 입력된 정보를 기반으로 HTML 이력서 생성
                    2. HTML을 PDF로 변환
                    3. S3에 PDF 업로드 후 URL 저장
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 작성 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "PDF 변환 또는 S3 업로드 실패")
    })
    @PostMapping
    public ResponseEntity<Void> saveResume(
            @Parameter(description = "이력서 작성 정보", required = true) @RequestBody
                    ResumePostDto resumePostDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.processResumePost(resumePostDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "이력서 파일 업로드",
            description =
                    """
                    기존에 작성된 PDF 이력서 파일을 직접 업로드합니다.

                    **제한 사항:**
                    - PDF 형식만 업로드 가능
                    - 파일과 함께 이력서 제목을 지정해야 합니다.
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "PDF 형식이 아닌 파일"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
        @ApiResponse(responseCode = "500", description = "S3 업로드 실패")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadResumeFile(
            @Parameter(description = "PDF 이력서 파일", required = true) @RequestParam("file")
                    MultipartFile file,
            @Parameter(description = "이력서 제목", required = true, example = "2024 상반기 백엔드 개발자 이력서")
                    @RequestParam("title")
                    String title,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        resumeService.uploadResumeFile(file, title, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 이력서 목록 조회", description = "현재 로그인한 사용자의 모든 이력서 목록을 조회합니다.")
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
                                                                        ResumeSummaryDto.class)))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @GetMapping
    public ResponseEntity<List<ResumeSummaryDto>> getAllMyResume(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getAllMyResume(userDetails));
    }

    @Operation(
            summary = "이력서 상세 조회",
            description =
                    """
                    특정 이력서의 상세 정보를 조회합니다.

                    **반환 정보:**
                    - 이력서 제목
                    - 생성일, 수정일
                    - PDF 열람용 Pre-signed URL (5분간 유효)

                    **권한:**
                    - 본인의 이력서만 조회 가능
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ResumeDetailDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 또는 권한 없음"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
    })
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDetailDto> getResumeById(
            @Parameter(description = "이력서 ID", required = true, example = "1")
                    @PathVariable("resumeId")
                    Long resumeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getResume(resumeId, userDetails));
    }

    @Operation(
            summary = "이력서 삭제",
            description =
                    """
                    특정 이력서를 삭제합니다.

                    **처리 과정:**
                    1. S3에 저장된 PDF 파일 삭제
                    2. DB에서 이력서 정보 삭제

                    **권한:**
                    - 본인의 이력서만 삭제 가능
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 또는 권한 없음"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "S3 파일 삭제 실패")
    })
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "이력서 ID", required = true, example = "1")
                    @PathVariable("resumeId")
                    Long resumeId) {
        resumeService.deleteResume(resumeId, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "매칭 결과 피드백 제출",
            description =
                    """
                    이력서 매칭 결과에 대한 피드백을 제출합니다.

                    **피드백 유형:**
                    - LIKE: 결과가 마음에 들어요
                    - RESUME_MISMATCH: 제 이력서와 맞지 않아요
                    - FIELD_MISMATCH: 제 분야와 맞지 않아요
                    - COMPANY_MISMATCH: 회사가 마음에 들지 않아요
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "피드백 제출 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 또는 권한 없음"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
    })
    @DeleteMapping("/{resumeId}/review")
    public ResponseEntity<Void> submitReview(
            @Parameter(description = "이력서 ID", required = true, example = "1")
                    @PathVariable("resumeId")
                    Long resumeId,
            @Parameter(description = "피드백 정보", required = true) @Valid @RequestBody
                    ReviewRequestDto reviewRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.submitReview(resumeId, reviewRequestDto, userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "AI 채용공고 매칭",
            description =
                    """
                    이력서를 AI로 분석하여 적합한 채용공고를 매칭합니다.

                    **매칭 과정:**
                    1. S3에서 이력서 PDF를 가져와 FastAPI 서버로 전송
                    2. OCR 및 AI 분석을 통해 적합한 공고 추출
                    3. 매칭 결과 저장 및 반환

                    **매칭 유형:**
                    - SUITABLE: 현재 역량으로 지원 가능
                    - GROWTH_TRACK: 일부 역량 보충 필요

                    **주의:**
                    - 기존 매칭 결과는 삭제되고 새로운 결과로 대체됩니다.
                    - 처리 시간이 다소 소요될 수 있습니다 (최대 5분).
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "매칭 성공",
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
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음 또는 PDF 파일 없음"),
        @ApiResponse(responseCode = "500", description = "외부 API 호출 실패")
    })
    @PostMapping("/{resumeId}/match")
    public ResponseEntity<List<MatchingResponseDto>> matchResume(
            @Parameter(description = "이력서 ID", required = true, example = "1")
                    @PathVariable("resumeId")
                    Long resumeId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MatchingResponseDto> matchingResponseDtoList =
                resumeService.matchResume(resumeId, userDetails);
        return ResponseEntity.ok(matchingResponseDtoList);
    }

    @Operation(
            summary = "FastAPI 서버 상태 확인",
            description =
                    """
                    AI 매칭 서버(FastAPI)의 연결 상태를 확인합니다.

                    **응답:**
                    - status: UP (정상) / DOWN (비정상)
                    - message: 상태 메시지
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "서버 정상",
                content =
                        @Content(
                                mediaType = "application/json",
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": \"UP\", \"message\":"
                                                                + " \"FastAPI is responding.\"}"))),
        @ApiResponse(
                responseCode = "503",
                description = "서버 연결 실패",
                content =
                        @Content(
                                mediaType = "application/json",
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": \"DOWN\", \"message\":"
                                                                + " \"Connection refused\"}")))
    })
    @SecurityRequirements
    @GetMapping("/health-check")
    public ResponseEntity<Map<String, String>> checkFastApi() {
        Map<String, String> healthStatus = resumeService.checkFastApiHealth();

        if ("UP".equals(healthStatus.get("status"))) {
            return ResponseEntity.ok(healthStatus);
        } else {
            return ResponseEntity.status(503).body(healthStatus);
        }
    }
}
