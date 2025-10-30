package com.resumefit.resumefit_backend.domain.user.service;

import com.resumefit.resumefit_backend.domain.resume.service.S3Service;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.domain.user.dto.UserInfoDto;
import com.resumefit.resumefit_backend.domain.user.entity.User;
import com.resumefit.resumefit_backend.domain.user.mapper.UserMapper;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    public UserInfoDto getUserInfo(CustomUserDetails userDetails) {

        Long id = userDetails.getId();

        User user = userRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        UserInfoDto userInfoDto = userMapper.toUserInfoDto(user);
        if (user.getPhotoKey() != null) {
            userInfoDto.setPhoto(s3Service.getFileUrl(user.getPhotoKey()));
        }
        return userInfoDto;
    }

    public UserInfoDto setUserInfo(CustomUserDetails userDetails, UserInfoDto userInfoDto, String fileKey) {

        Long id = userDetails.getId();

        User user = userRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        userMapper.updateUserFromDto(userInfoDto, user);

        if (fileKey != null) {
            if (user.getPhotoKey() != null) {
                s3Service.deleteFile(user.getPhotoKey()); // 기존 파일 삭제
            }
            user.setPhotoKey(fileKey);
        }

        User updatedUser = userRepository.save(user);

        UserInfoDto upadatedUserInfoDto = userMapper.toUserInfoDto(updatedUser);
        if (user.getPhotoKey() != null) {
            upadatedUserInfoDto.setPhoto(s3Service.getFileUrl(user.getPhotoKey()));
        }

        return upadatedUserInfoDto;
    }

}
