package com.resumefit.resumefit_backend.domain.skill.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "요건 유형", enumAsRef = true)
public enum SkillType {
    @Schema(description = "필수 기술 (자격요건)")
    REQUIRED,

    @Schema(description = "우대 기술 (우대사항)")
    PREFERRED
}
