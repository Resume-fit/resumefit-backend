package com.resumefit.resumefit_backend.domain.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumePostDto {

    private String resumeTitle; // 이력서 제목

    private String profileImageS3Url; // 프로필 사진 S3 주소
    private String introduction; // 간단 자기소개

    // 학력
    private List<EducationDto> education;

    // 경력
    private List<ExperienceDto> experience;

    // 기술 스택
    private List<String> skills;

    // 자격증
    private List<CertificateDto> certificates;

    // 프로젝트
    private List<ProjectDto> projects;

    // 수상/활동
    private List<AwardDto> awardsActivities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationDto {

        private String schoolName;
        private String major;
        private String degree;
        private String status;
        private LocalDate startDate; 
        private LocalDate endDate;
        private Double gpa; // 내 학점 (예: 4.0)
        private Double maxGpa; // 최대 학점 (예: 4.5)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceDto {

        private String companyName;
        private String position;
        private LocalDate startDate;
        private LocalDate endDate; // 재직 중이면 null
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificateDto {

        private String certificateName;
        private LocalDate acquisitionDate; // 취득일
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectDto {

        private String projectName;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String projectUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AwardDto {

        private String activityName;
        private String organization;
        private String description;
        private LocalDate date; // 날짜
    }
}
