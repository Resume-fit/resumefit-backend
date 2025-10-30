package com.resumefit.resumefit_backend.domain.user.controller;

import com.resumefit.resumefit_backend.domain.resume.service.S3Service;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.domain.user.dto.UserInfoDto;
import com.resumefit.resumefit_backend.domain.user.service.UserService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;

    @GetMapping
    private ResponseEntity<UserInfoDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails));
    }

    @PatchMapping
    public ResponseEntity<UserInfoDto> setUserInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestPart(value = "userInfoDto", required = false) UserInfoDto userInfoDto,
        @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException
    {
        if (userInfoDto == null) {
            userInfoDto = new UserInfoDto(); // 빈 객체 생성 또는 기존 정보 조회
        }
        String fileKey = null;

        if (photo != null && !photo.isEmpty()) {
            fileKey = s3Service.uploadFile(photo);
        }
        UserInfoDto updatedUserInfo = userService.setUserInfo(userDetails, userInfoDto, fileKey);
        return ResponseEntity.ok(updatedUserInfo);
    }
}