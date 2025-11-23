package com.resumefit.resumefit_backend.auth.controller;

import com.resumefit.resumefit_backend.auth.dto.ReissueResponseDto;
import com.resumefit.resumefit_backend.auth.entity.RefreshToken;
import com.resumefit.resumefit_backend.auth.service.RefreshTokenService;
import com.resumefit.resumefit_backend.auth.util.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인, 로그아웃 등 인증 관련 API")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "토큰 재발급",
            description =
                    """
                    Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.

                    - 쿠키에 저장된 Refresh Token을 검증합니다.
                    - 유효한 경우 새로운 Access Token과 Refresh Token을 발급합니다.
                    - 새 Refresh Token은 쿠키에 자동으로 설정됩니다.

                    **사용 시점**: Access Token이 만료되었을 때 자동으로 호출
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "토큰 재발급 성공",
                content = @Content(schema = @Schema(implementation = ReissueResponseDto.class))),
        @ApiResponse(
                responseCode = "401",
                description = "Refresh Token이 없거나 유효하지 않음",
                content =
                        @Content(
                                mediaType = "text/plain",
                                examples = {
                                    @ExampleObject(
                                            name = "토큰 없음",
                                            value = "Refresh token is required."),
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "유효하지 않은 토큰입니다."),
                                    @ExampleObject(name = "만료된 토큰", value = "만료된 토큰입니다.")
                                }))
    })
    @SecurityRequirements
    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 1. 요청의 쿠키에서 리프레쉬 토큰 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return new ResponseEntity<>("Refresh token is required.", HttpStatus.UNAUTHORIZED);
        }

        try {
            RefreshToken storedToken = refreshTokenService.validateRefreshToken(refreshToken);

            String username = jwtUtil.getUsername(storedToken.getTokenValue());
            String role = jwtUtil.getRole(storedToken.getTokenValue());

            String newAccessToken = jwtUtil.createJwt(username, role, 60 * 60 * 1000L); // 1시간 유효

            String newRefreshToken = refreshTokenService.createAndSaveRefreshToken(username, role);

            response.addCookie(createCookie("refresh", newRefreshToken));
            return ResponseEntity.ok(new ReissueResponseDto(newAccessToken));

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효기간 7일
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // 배포 시 HTTPS 환경에서 주석 해제
        return cookie;
    }
}
