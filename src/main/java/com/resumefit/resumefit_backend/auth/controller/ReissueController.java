package com.resumefit.resumefit_backend.auth.controller;

import com.resumefit.resumefit_backend.auth.dto.ReissueResponseDto;
import com.resumefit.resumefit_backend.auth.entity.RefreshToken;
import com.resumefit.resumefit_backend.auth.service.RefreshTokenService;
import com.resumefit.resumefit_backend.auth.util.JWTUtil;

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
@Tag(name = "토근 재발급 api", description = "토근 재발급과 관련된 API들입니다.")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

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
