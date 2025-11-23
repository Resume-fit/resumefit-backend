package com.resumefit.resumefit_backend.domain.user.controller;

import com.resumefit.resumefit_backend.domain.resume.service.S3Service;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.domain.user.dto.UserInfoDto;
import com.resumefit.resumefit_backend.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "회원", description = "회원가입, 회원정보 조회/수정 관련 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @Operation(
            summary = "내 정보 조회",
            description =
                    """
                    현재 로그인한 사용자의 정보를 조회합니다.

                    **반환 정보:**
                    - 이름, 이메일, 전화번호
                    - 프로필 사진 URL
                    - 학력 정보 (학력, 학교명, 전공)
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping
    private ResponseEntity<UserInfoDto> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails));
    }

    @Operation(
            summary = "내 정보 수정",
            description =
                    """
                    현재 로그인한 사용자의 정보를 수정합니다.

                    **수정 가능 항목:**
                    - 이름, 전화번호
                    - 학력 정보 (학력, 학교명, 전공)
                    - 프로필 사진

                    **참고:**
                    - 이메일은 수정할 수 없습니다.
                    - 입력하지 않은 필드는 기존 값이 유지됩니다.
                    - 프로필 사진을 변경하면 기존 사진은 삭제됩니다.
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "파일 업로드 실패")
    })
    @PatchMapping
    public ResponseEntity<UserInfoDto> setUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "userInfoDto", required = false) UserInfoDto userInfoDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo)
            throws IOException {
        if (userInfoDto == null) {
            userInfoDto = new UserInfoDto(); // 빈 객체 생성 또는 기존 정보 조회
        }
        String fileKey = null;

        if (photo != null && !photo.isEmpty()) {
            fileKey = s3Service.uploadFile(photo);
        }
        UserInfoDto updatedUserInfo = userService.setUserInfo(userDetails, userInfoDto, fileKey);
        return ResponseEntity.ok(updatedUserInfo);
    }
}
