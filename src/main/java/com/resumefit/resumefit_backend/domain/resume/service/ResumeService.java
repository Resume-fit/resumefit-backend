package com.resumefit.resumefit_backend.domain.resume.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.resumefit.resumefit_backend.domain.resume.dto.PDFInfoDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeDetailDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumePostDto;
import com.resumefit.resumefit_backend.domain.resume.dto.ResumeSummaryDto;
import com.resumefit.resumefit_backend.domain.resume.entity.Resume;
import com.resumefit.resumefit_backend.domain.resume.mapper.ResumeMapper;
import com.resumefit.resumefit_backend.domain.resume.repository.ResumeRepository;
import com.resumefit.resumefit_backend.domain.user.dto.CustomUserDetails;
import com.resumefit.resumefit_backend.domain.user.entity.User;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;
import com.resumefit.resumefit_backend.exception.CustomException;
import com.resumefit.resumefit_backend.exception.ErrorCode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final HtmlGenerationService htmlGenerationService;
    private final ResumeMapper resumeMapper;

    public void processResumePost(ResumePostDto resumePostDto, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String photoBase64 = null;
//        if (user.getPhotoKey() != null && !user.getPhotoKey().isBlank()) {
//            byte[] photoBytes = s3Service.downloadFileAsBytes(user.getPhotoKey());
//            if (photoBytes != null) {
//                photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
//            }
//        }
        // --- ⬇️ 3. 디버깅 로그 추가 ⬇️ ---
        log.info("PDF 생성 시작 - User: {}", user.getEmail());

        if (user.getPhotoKey() != null && !user.getPhotoKey().isBlank()) {
            log.info("발견된 Photo Key: {}", user.getPhotoKey());

            byte[] photoBytes = s3Service.downloadFileAsBytes(user.getPhotoKey());

            if (photoBytes != null && photoBytes.length > 0) {
                log.info("S3에서 이미지 다운로드 성공, Bytes: {}", photoBytes.length);
                photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                log.info("Base64 인코딩 완료 (앞 20자): {}", photoBase64.substring(0, 20));
            } else {
                log.warn("S3에서 파일을 다운로드했으나 비어있거나 null입니다. Key: {}", user.getPhotoKey());
            }
        } else {
            log.warn("User 엔티티에 photoKey가 없습니다. 프로필 사진을 건너뜁니다.");
        }
        // --- ⬆️ 디버깅 로그 끝 ⬆️ ---

        String htmlContent = htmlGenerationService.generateResumeHtml(user, resumePostDto,
            photoBase64);

        PDFInfoDto pdfInfoDto = htmlToPdf(htmlContent, user, resumePostDto);

        Resume resume = Resume.builder()
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

            FontProgram fontProgram = FontProgramFactory.createFont(fontBytes,
                true); // true = embed
            fontProvider.addFont(fontProgram);

            properties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, outputStream, properties);
            pdfBytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.PDF_CONVERSION_FAILED);
        }

        // PDF 파일을 S3에 업로드하고 파일 키(이름) 받기
        String fileKey;
        try {
            // S3에 저장될 파일명 생성
            // 파일명에 특수문자나 공백이 있다면 제거하거나 URL 인코딩 필요
            String desiredFileName = user.getName().replaceAll("[^a-zA-Z0-9가-힣]", "_") // 특수문자 제거
                + "_" + resumePostDto.getResumeTitle().replaceAll("[^a-zA-Z0-9가-힣]", "_")
                + "_" + System.currentTimeMillis() + ".pdf";
            fileKey = s3Service.uploadPdfBytes(pdfBytes, desiredFileName);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
        return PDFInfoDto.builder()
            .fileUrl(s3Service.getFileUrl(fileKey))
            .fileKey(fileKey)
            .build();
    }

    public List<ResumeSummaryDto> getAllMyResume(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<Resume> resumeList = resumeRepository.findByUser(user);
        return resumeMapper.toResumeSummaryDtoList(resumeList);
    }

    public void deleteResume(Long resumeId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        // Resume 엔티티 조회
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        // 현재 사용자와 Resume 소유자 확인
        if (!resume.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // S3 파일 삭제
        if (resume.getFileKey() != null && !resume.getFileKey().isBlank()) {
            try {
                s3Service.deleteFile(resume.getFileKey());
            } catch (Exception e) {
                throw new CustomException(ErrorCode.S3_DELETE_FAILED);
            }
        }

        // Resume 엔티티 삭제
        resumeRepository.delete(resume);
    }

    @Transactional(readOnly = true) // DB 읽기 전용
    public ResumeDetailDto getResume(Long resumeId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        // 1. 이력서가 존재하는지 조회
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        // 2. 현재 로그인한 사용자가 이력서의 소유자인지 확인
        if (!resume.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 3. (추가) 이력서의 fileKey로 Pre-signed URL 생성
        String fileKey = resume.getFileKey();
        String presignedUrl = null; // 기본값 null

        if (fileKey != null && !fileKey.isBlank()) {
            // S3Service를 통해 5분간 유효한 임시 URL 생성
            presignedUrl = s3Service.generatePresignedUrl(fileKey, Duration.ofMinutes(5));
        } else {
            // 이력서는 존재하나 연결된 PDF 파일 키가 없는 경우 (로깅)
            log.warn("Resume ID {} has no S3 fileKey.", resumeId);
        }

        // 4. DTO로 변환하여 반환 (Builder 사용)
        return ResumeDetailDto.builder()
            .title(resume.getTitle())
            .fileKey(resume.getFileKey()) // 관리용 키
            .createdAt(resume.getCreatedAt())
            .updatedAt(resume.getUpdatedAt())
            .pdfViewUrl(presignedUrl) // ✅ PDF를 볼 수 있는 URL
            .build();
    }
}