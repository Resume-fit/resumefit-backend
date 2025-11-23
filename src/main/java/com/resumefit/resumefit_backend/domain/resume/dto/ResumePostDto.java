package com.resumefit.resumefit_backend.domain.resume.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이력서 작성 요청 DTO")
public class ResumePostDto {

    @Schema(description = "이력서 제목", example = "2024 상반기 백엔드 개발자 이력서")
    private String resumeTitle;

    @Schema(description = "프로필 사진 S3 URL (사전 업로드된 경우)", example = "https://bucket.s3.amazonaws.com/photo.jpg")
    private String profileImageS3Url;

    @Schema(description = "간단 자기소개", example = "3년차 백엔드 개발자로 Spring Boot와 JPA를 주로 사용합니다.")
    private String introduction;

    @Schema(description = "학력 사항 목록")
    private List<EducationDto> education;

    @Schema(description = "경력 사항 목록")
    private List<ExperienceDto> experience;

    @Schema(description = "기술 스택 목록", example = "[\"Java\", \"Spring Boot\", \"JPA\", \"MySQL\"]")
    private List<String> skills;

    @Schema(description = "자격증 목록")
    private List<CertificateDto> certificates;

    @Schema(description = "프로젝트 목록")
    private List<ProjectDto> projects;

    @Schema(description = "수상/활동 목록")
    private List<AwardDto> awardsActivities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "학력 정보")
    public static class EducationDto {

        @Schema(description = "학교명", example = "명지대학교")
        private String schoolName;

        @Schema(description = "전공", example = "융합소프트웨어학부")
        private String major;

        @Schema(description = "학위", example = "학사")
        private String degree;

        @Schema(description = "재학 상태", example = "졸업")
        private String status;

        @Schema(description = "입학일", example = "2020-03-01")
        private LocalDate startDate;

        @Schema(description = "졸업일", example = "2024-02-28")
        private LocalDate endDate;

        @Schema(description = "취득 학점", example = "4.0")
        private Double gpa;

        @Schema(description = "최대 학점", example = "4.5")
        private Double maxGpa;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "경력 정보")
    public static class ExperienceDto {

        @Schema(description = "회사명", example = "(주)테크컴퍼니")
        private String companyName;

        @Schema(description = "직무/직책", example = "백엔드 개발자")
        private String position;

        @Schema(description = "입사일", example = "2022-03-01")
        private LocalDate startDate;

        @Schema(description = "퇴사일 (재직 중이면 null)", example = "2024-02-28")
        private LocalDate endDate;

        @Schema(description = "담당 업무 설명", example = "MSA 기반 결제 시스템 개발 및 운영")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자격증 정보")
    public static class CertificateDto {

        @Schema(description = "자격증명", example = "정보처리기사")
        private String certificateName;

        @Schema(description = "취득일", example = "2023-06-15")
        private LocalDate acquisitionDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "프로젝트 정보")
    public static class ProjectDto {

        @Schema(description = "프로젝트명", example = "ResumeFit - AI 기반 이력서 매칭 서비스")
        private String projectName;

        @Schema(description = "프로젝트 설명", example = "Spring Boot 기반 백엔드 개발, FastAPI 연동, S3 파일 관리 구현")
        private String description;

        @Schema(description = "시작일", example = "2024-01-01")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2024-06-30")
        private LocalDate endDate;

        @Schema(description = "프로젝트 URL (GitHub 등)", example = "https://github.com/user/resumefit")
        private String projectUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "수상/활동 정보")
    public static class AwardDto {

        @Schema(description = "활동명/수상명", example = "캡스톤 디자인 경진대회 대상")
        private String activityName;

        @Schema(description = "주관 기관", example = "명지대학교 ICT융합대학")
        private String organization;

        @Schema(description = "활동 설명", example = "AI 기반 이력서 매칭 서비스 개발로 대상 수상")
        private String description;

        @Schema(description = "활동/수상 날짜", example = "2024-06-15")
        private LocalDate date;
    }
}