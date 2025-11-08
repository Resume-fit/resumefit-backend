package com.resumefit.resumefit_backend.domain.jobposition.dto;

import com.resumefit.resumefit_backend.domain.jobposition.entity.Requirement;
import com.resumefit.resumefit_backend.domain.jobposition.entity.SkillType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class JobPositionDetailDto {

    private String positionName;
    private String companyName;
    private String jobCategory;
    private String workPlace;
    private String employmentType;
    private String url;

    private String mainJob;

    private List<RequirementDto> requirements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequirementDto {
        private SkillType type; // REQUIRED 또는 PREFERRED
        private String content; // "Spring Boot 2년 이상 경험자" 등
    }

}
