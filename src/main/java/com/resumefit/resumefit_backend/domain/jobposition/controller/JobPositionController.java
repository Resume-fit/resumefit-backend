package com.resumefit.resumefit_backend.domain.jobposition.controller;

import com.resumefit.resumefit_backend.domain.jobposition.dto.JobPositionSummaryDto;
import com.resumefit.resumefit_backend.domain.jobposition.service.JobPositionService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-positions")
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @Operation(summary = "채용공고 조회", description = "모든 채용공고를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<JobPositionSummaryDto>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionService.getAllJobPositions());
    }

    @Operation(summary = "직무별 채용공고 조회", description = "직무명을 통해 채용공고를 조회합니다.")
    @GetMapping("/{category}")
    public ResponseEntity<List<JobPositionSummaryDto>> getAllJobPositionsByCategory(
        @PathVariable("category") String category) {
        return ResponseEntity.ok(jobPositionService.getAllJobPositionsByCategory(category));
    }

}
