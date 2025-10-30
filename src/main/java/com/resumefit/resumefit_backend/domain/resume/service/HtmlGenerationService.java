package com.resumefit.resumefit_backend.domain.resume.service;

import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class HtmlGenerationService { // Service 이름은 예시입니다.

    private final TemplateEngine templateEngine; // Thymeleaf TemplateEngine 주입

    public String generateResumeHtml(User user, ResumePostDto resumeDto, String photoBase64) {
        // 1. Thymeleaf Context 생성
        Context context = new Context();

        // 2. Context에 모델(데이터) 주입
        //    HTML 템플릿에서 사용하는 이름(${user}, ${resume})과 동일하게 설정
        context.setVariable("user", user);
        context.setVariable("resume", resumeDto);
        context.setVariable("photoBase64", photoBase64);

        // 3. 템플릿 엔진을 사용하여 HTML 렌더링
        //    "resume-template"은 src/main/resources/templates/ 폴더 아래의
        //    resume-template.html 파일을 의미

        return templateEngine.process("resume-template", context);
    }
}