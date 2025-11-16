package com.resumefit.resumefit_backend.domain.matching.service;

import com.resumefit.resumefit_backend.domain.matching.mapper.MatchingMapper;
import com.resumefit.resumefit_backend.domain.matching.repository.MatchingRepository;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.resume.repository.ResumeRepository;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final ResumeRepository resumeRepository;
    private final MatchingMapper matchingMapper;

    public List<MatchingResponseDto> getMatching(Long resumeId, CustomUserDetails userDetails) {
        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return matchingMapper.toMatchingResponseDtoList(matchingRepository.findAllByResume(resume));
    }
}
