package com.resumefit.resumefit_backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class JoinRequestDto {

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "생년월일", example = "1995-03-15")
    private LocalDate birth;

    @Schema(
        description = "전화번호 ('-' 없이 10~11자리)",
        example = "01012345678",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 '-' 없이 10~11자리 숫자로 입력해주세요.")
    private String phoneNumber;

    @Schema(description = "이메일 (로그인 ID로 사용)", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Schema(
        description = "비밀번호 (8자 이상, 영문/숫자/특수문자 포함)",
        example = "Password123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,64}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    @Schema(description = "최종 학력", example = "BACHELOR_DEGREE")
    private AcademicBackground academic;

    @Schema(description = "학교명", example = "명지대학교")
    private String schoolName;

    @Schema(description = "전공", example = "융합소프트웨어학부")
    private String major;
}