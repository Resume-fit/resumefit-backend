package com.resumefit.resumefit_backend.domain.user.controller;

import com.resumefit.resumefit_backend.domain.resume.service.S3Service;
import com.resumefit.resumefit_backend.domain.user.dto.JoinRequestDto;
import com.resumefit.resumefit_backend.domain.user.service.JoinService;

import jakarta.validation.Valid;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final S3Service s3Service;

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(
        @Valid @RequestPart("joinDto") JoinRequestDto joinDto,
        @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {

        String fileKey = null;

        if (photo != null && !photo.isEmpty()) {
            fileKey = s3Service.uploadFile(photo);
        }

        joinService.joinProcess(joinDto, fileKey);
        return ResponseEntity.ok("회원가입 요청이 성공적으로 완료되었습니다.");
    }
}
