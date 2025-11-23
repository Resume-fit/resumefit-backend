package com.resumefit.resumefit_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 재발급 응답 DTO")
public class ReissueResponseDto {

    @Schema(
            description = "새로 발급된 JWT Access Token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
}
