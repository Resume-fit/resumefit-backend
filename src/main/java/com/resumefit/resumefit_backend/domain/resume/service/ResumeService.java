package com.resumefit.resumefit_backend.domain.resume.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.entity.JobPosition;
import com.resumefit.resumefit_backend.domain.jobposition.mapper.JobPositionMapper;
import com.resumefit.resumefit_backend.domain.jobposition.repository.JobPositionRepository;
import com.resumefit.resumefit_backend.domain.matching.dto.AiMatchDetailDto;
import com.resumefit.resumefit_backend.domain.matching.entity.Matching;
import com.resumefit.resumefit_backend.domain.matching.repository.MatchingRepository;
import com.resumefit.resumefit_backend.domain.resume.dto.MatchingResponseDto;
import com.resumefit.resumefit_backend.domain.resume.dto.PDFInfoDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeDetailDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.resume.mapper.ResumeMapper;
import com.resumefit.resumefit_backend.domain.resume.repository.ResumeRepository;
import com.resumefit.resumefit_backend.domain.review.repository.ReviewRepository;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.domain.user.entity.User;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final HtmlGenerationService htmlGenerationService;
    private final ResumeMapper resumeMapper;
    private final MatchingRepository matchingRepository;
    private final ReviewRepository reviewRepository;
    private final JobPositionRepository jobPositionRepository;
    private final RestClient fastApiRestClient;
    private final JobPositionMapper jobPositionMapper;

    @Transactional
    public void processResumePost(ResumePostDto resumePostDto, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String photoBase64 = null;
        if (user.getPhotoKey() != null && !user.getPhotoKey().isBlank()) {
            byte[] photoBytes = s3Service.downloadFileAsBytes(user.getPhotoKey());
            if (photoBytes != null) {
                photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
            }
        }

        String htmlContent =
                htmlGenerationService.generateResumeHtml(user, resumePostDto, photoBase64);

        PDFInfoDto pdfInfoDto = htmlToPdf(htmlContent, user, resumePostDto);

        Resume resume =
                Resume.builder()
                        .user(user)
                        .title(resumePostDto.getResumeTitle())
                        .fileUrl(pdfInfoDto.getFileUrl()) // S3 URL 저장
                        .fileKey(pdfInfoDto.getFileKey()) // S3 Key 저장 (삭제 시 필요)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        Resume savedResume = resumeRepository.save(resume);
    }

    private PDFInfoDto htmlToPdf(String htmlContent, User user, ResumePostDto resumePostDto) {
        byte[] pdfBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider(false, false, false);

            ClassPathResource fontResource = new ClassPathResource("fonts/NotoSansKR-Regular.ttf");
            byte[] fontBytes = fontResource.getInputStream().readAllBytes();

            FontProgram fontProgram =
                    FontProgramFactory.createFont(fontBytes, true); // true = embed
            fontProvider.addFont(fontProgram);

            properties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, outputStream, properties);
            pdfBytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.PDF_CONVERSION_FAILED);
        }

        String fileKey;
        try {
            String desiredFileName =
                    user.getName().replaceAll("[^a-zA-Z0-9가-힣]", "_") // 특수문자 제거
                            + "_"
                            + resumePostDto.getResumeTitle().replaceAll("[^a-zA-Z0-9가-힣]", "_")
                            + "_"
                            + System.currentTimeMillis()
                            + ".pdf";
            fileKey = s3Service.uploadPdfBytes(pdfBytes, desiredFileName);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
        return PDFInfoDto.builder().fileUrl(s3Service.getFileUrl(fileKey)).fileKey(fileKey).build();
    }

    @Transactional(readOnly = true)
    public List<ResumeSummaryDto> getAllMyResume(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<Resume> resumeList = resumeRepository.findByUser(user);
        return resumeMapper.toResumeSummaryDtoList(resumeList);
    }

    @Transactional
    public void deleteResume(Long resumeId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        log.info("=== 이력서 삭제 시작 ===");
        log.info("Resume ID: {}, User ID: {}", resumeId, userId);

        // Resume 엔티티 조회
        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        // 현재 사용자와 Resume 소유자 확인
        if (!resume.getUser().getId().equals(userId)) {
            log.error("권한 없음 - Resume Owner: {}, Requester: {}", resume.getUser().getId(), userId);
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        try {
            // 1. 리뷰 먼저 삭제 (Foreign Key 제약 조건 해결)
            log.info("리뷰 삭제 시작 - Resume ID: {}", resumeId);
            reviewRepository.deleteByResume(resume);
            log.info("리뷰 삭제 완료");

            // 2. 매칭 결과 삭제 (Foreign Key 제약 조건 해결)
            log.info("매칭 결과 삭제 시작 - Resume ID: {}", resumeId);
            matchingRepository.deleteByResume(resume);
            log.info("매칭 결과 삭제 완료");

            // 3. S3 파일 삭제
            if (resume.getFileKey() != null && !resume.getFileKey().isBlank()) {
                log.info("S3 파일 삭제 시작 - Key: {}", resume.getFileKey());
                s3Service.deleteFile(resume.getFileKey());
                log.info("S3 파일 삭제 완료");
            }

            // 4. 이력서 삭제
            log.info("이력서 DB 삭제 시작 - Resume ID: {}", resumeId);
            resumeRepository.delete(resume);
            log.info("이력서 삭제 완료 - Resume ID: {}", resumeId);

        } catch (Exception e) {
            log.error("이력서 삭제 실패 - Resume ID: {}, Error: {}", resumeId, e.getMessage(), e);
            throw new CustomException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    @Transactional(readOnly = true) // DB 읽기 전용
    public ResumeDetailDto getResume(Long resumeId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 이력서의 fileKey로 Pre-signed URL 생성
        String fileKey = resume.getFileKey();
        String presignedUrl = null; // 기본값 null

        if (fileKey != null && !fileKey.isBlank()) {
            // S3Service를 통해 5분간 유효한 임시 URL 생성
            presignedUrl = s3Service.generatePresignedUrl(fileKey, Duration.ofMinutes(5));
        } else {
            // 이력서는 존재하나 연결된 PDF 파일 키가 없는 경우
            log.warn("Resume ID {} has no S3 fileKey.", resumeId);
        }

        return ResumeDetailDto.builder()
                .title(resume.getTitle())
                .fileKey(resume.getFileKey())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .pdfViewUrl(presignedUrl)
                .build();
    }

    @Transactional
    public void uploadResumeFile(MultipartFile file, String title, CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (file.isEmpty() || !Objects.equals(file.getContentType(), "application/pdf")) {
            throw new CustomException(ErrorCode.NOT_A_PDF_FILE);
        }

        String fileKey;
        try {
            fileKey = s3Service.uploadFile(file);
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패 (IOException): {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패 (Exception): {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }

        // PDFInfoDto 생성
        PDFInfoDto pdfInfoDto =
                PDFInfoDto.builder()
                        .fileUrl(s3Service.getFileUrl(fileKey))
                        .fileKey(fileKey)
                        .build();

        Resume resume =
                Resume.builder()
                        .user(user)
                        .title(title)
                        .fileUrl(pdfInfoDto.getFileUrl()) // S3 URL 저장
                        .fileKey(pdfInfoDto.getFileKey()) // S3 Key 저장 (삭제 시 필요)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        resumeRepository.save(resume);
    }

    @Transactional
    public List<MatchingResponseDto> matchResume(Long resumeId, CustomUserDetails userDetails) {

        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String fileKey = resume.getFileKey();
        if (fileKey == null || fileKey.isBlank()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        String s3UrlForFastApi = s3Service.generatePresignedUrl(fileKey, Duration.ofMinutes(2));

        log.info("FastAPI 매칭 요청 시작. Resume ID: {}, S3 URL: {}", resumeId, s3UrlForFastApi);

        try {
            Map<String, String> requestBody = Map.of("full_path", s3UrlForFastApi);

            Map<String, List<AiMatchDetailDto>> aiResponse =
                    fastApiRestClient
                            .post()
                            .uri("/api/ocr")
                            .body(requestBody)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            Map<String, List<AiMatchDetailDto>>>() {});

            log.info("Deleting old matches for Resume ID: {}", resumeId);
            matchingRepository.deleteByResume(resume);

            List<Matching> newMatches = new ArrayList<>();

            // "SUITABLE" 목록 처리
            assert aiResponse != null;
            if (aiResponse.containsKey("SUITABLE")) {
                for (AiMatchDetailDto dto : aiResponse.get("SUITABLE")) {
                    newMatches.add(createMatchingEntity(resume, dto));
                }
            }

            // "GROWTH_TRACK" 목록 처리
            if (aiResponse.containsKey("GROWTH_TRACK")) {
                for (AiMatchDetailDto dto : aiResponse.get("GROWTH_TRACK")) {
                    newMatches.add(createMatchingEntity(resume, dto));
                }
            }

            List<Matching> savedMatches =
                    matchingRepository.saveAll(
                            newMatches.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList()));
            log.info("Saved {} new matches for Resume ID: {}", savedMatches.size(), resumeId);

            return savedMatches.stream()
                    .map(
                            match -> {
                                JobPosition job = match.getJobPosition();

                                JobPositionSummaryDto summaryDto =
                                        jobPositionMapper.toJobPositionSummaryDto(job);

                                return new MatchingResponseDto(
                                        summaryDto,
                                        match.getMatchType().name(), // Enum -> String
                                        match.getComment());
                            })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(
                    "FastAPI 매칭 호출 또는 저장 실패. Resume ID: {}. Error: {}",
                    resumeId,
                    e.getMessage(),
                    e);
            throw new CustomException(ErrorCode.EXTERNAL_API_CALL_FAILED);
        }
    }

    private Matching createMatchingEntity(Resume resume, AiMatchDetailDto dto) {
        // DTO의 jobPositionId로 실제 JobPosition 엔티티 조회
        JobPosition jobPosition =
                jobPositionRepository.findById(dto.getJobPositionId()).orElse(null); // ID가 없으면 null

        if (jobPosition == null) {
            log.warn(
                    "Matching result returned non-existent JobPosition ID: {}",
                    dto.getJobPositionId());
            return null; // 존재하지 않는 공고 ID는 건너뜀
        }

        return Matching.builder()
                .resume(resume)
                .jobPosition(jobPosition)
                .matchType(dto.getMatchType()) // DTO에서 이미 Enum으로 변환됨
                .comment(dto.getComment())
                .build();
    }

    public Map<String, String> checkFastApiHealth() {
        try {
            fastApiRestClient.get().uri("/").retrieve().toBodilessEntity(); // 응답 코드가 2xx인지 확인

            // 성공 시
            log.info("FastAPI health check successful.");
            return Map.of("status", "UP", "message", "FastAPI is responding.");

        } catch (RestClientException e) {
            // 연결 실패, 5xx 에러 등 모든 RestClient 예외
            log.error("FastAPI health check failed: {}", e.getMessage());
            return Map.of("status", "DOWN", "message", e.getMessage());
        }
    }
}
