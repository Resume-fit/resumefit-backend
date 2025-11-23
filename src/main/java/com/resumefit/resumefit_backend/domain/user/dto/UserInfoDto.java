package com.resumefit.resumefit_backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "사용자 정보 DTO")
public class UserInfoDto {

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(
            description = "프로필 사진 URL",
            example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uuid_photo.jpg")
    private String photo;

    @Schema(description = "최종 학력", example = "BACHELOR_DEGREE")
    private String academic;

    @Schema(description = "학교명", example = "명지대학교")
    private String schoolName;

    @Schema(description = "전공", example = "융합소프트웨어학부")
    private String major;
}
