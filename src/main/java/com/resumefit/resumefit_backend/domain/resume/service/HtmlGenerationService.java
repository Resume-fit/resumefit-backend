package com.resumefit.resumefit_backend.domain.resume.service;

import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class HtmlGenerationService {

    private final TemplateEngine templateEngine;

    public String generateResumeHtml(User user, ResumePostDto resumeDto, String photoBase64) {
        Context context = new Context();

        context.setVariable("user", user);
        context.setVariable("resume", resumeDto);
        context.setVariable("photoBase64", photoBase64);

        return templateEngine.process("resume-template", context);
    }
}
