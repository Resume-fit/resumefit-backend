package com.resumefit.resumefit_backend.domain.resume.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PDFInfoDto {

    private String fileUrl;
    private String fileKey;
}
