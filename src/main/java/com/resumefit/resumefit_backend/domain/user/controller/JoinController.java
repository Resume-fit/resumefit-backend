package com.resumefit.resumefit_backend.domain.user.controller;

import com.resumefit.resumefit_backend.domain.resume.service.S3Service;
import com.resumefit.resumefit_backend.domain.user.dto.JoinRequestDto;
import com.resumefit.resumefit_backend.domain.user.service.JoinService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Tag(name = "회원", description = "회원가입, 회원정보 조회/수정 관련 API")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final S3Service s3Service;

    @Operation(
        summary = "회원가입",
        description = """
                    새로운 사용자를 등록합니다.
                    
                    **필수 입력 항목:**
                    - 이름, 이메일, 비밀번호, 전화번호
                    
                    **선택 입력 항목:**
                    - 생년월일, 학력, 학교명, 전공, 프로필 사진
                    
                    **비밀번호 규칙:**
                    - 최소 8자 이상
                    - 영문, 숫자, 특수문자 모두 포함
                    
                    **전화번호 형식:**
                    - '-' 없이 10~11자리 숫자 (예: 01012345678)
                    """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "회원가입 요청이 성공적으로 완료되었습니다.")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                                            {
                                                "email": "유효한 이메일 형식이 아닙니다.",
                                                "password": "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다."
                                            }
                                            """
                )
            )
        ),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    @SecurityRequirements
    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(
            @Valid @RequestPart("joinDto") JoinRequestDto joinDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo)
            throws IOException {

        String fileKey = null;

        if (photo != null && !photo.isEmpty()) {
            fileKey = s3Service.uploadFile(photo);
        }

        joinService.joinProcess(joinDto, fileKey);
        return ResponseEntity.ok("회원가입 요청이 성공적으로 완료되었습니다.");
    }
}
