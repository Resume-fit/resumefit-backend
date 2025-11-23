package com.resumefit.resumefit_backend.domain.user.service;

import com.resumefit.resumefit_backend.domain.user.dto.JoinRequestDto;
import com.resumefit.resumefit_backend.domain.user.entity.User;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinRequestDto joinDto, String fileKey) {
        if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User user = new User();

        user.setEmail(joinDto.getEmail());
        user.setName(joinDto.getName());
        user.setBirth(joinDto.getBirth());
        user.setPhoneNumber(joinDto.getPhoneNumber());
        user.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));
        user.setPhotoKey(fileKey);
        user.setAcademic(joinDto.getAcademic());
        user.setSchoolName(joinDto.getSchoolName());
        user.setMajor(joinDto.getMajor());
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }
}
