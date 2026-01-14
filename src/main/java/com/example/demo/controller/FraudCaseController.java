package com.example.demo.controller;

import com.example.demo.dto.response.CaseActionResponse;
import com.example.demo.dto.response.FraudCaseResponse;
import com.example.demo.entity.CaseStatus;
import com.example.demo.entity.FraudCase;
import com.example.demo.service.IFraudCaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fraud Case Controller
 * GET /fraud-cases - Tüm case'leri listeler
 * GET /fraud-cases/open - Açık case'leri listeler
 * GET /fraud-cases/{caseId} - Case detayı getirir (aksiyon geçmişi ile)
 * PATCH /fraud-cases/{caseId}/status - Case durumunu günceller
 */
@RestController
@RequestMapping("/fraud-cases")
public class FraudCaseController {

    private final IFraudCaseService fraudCaseService;

    public FraudCaseController(IFraudCaseService fraudCaseService) {
        this.fraudCaseService = fraudCaseService;
    }

    /**
     * Tüm fraud case'leri listeler
     * GET /fraud-cases
     */
    @GetMapping
    public ResponseEntity<List<FraudCaseResponse>> getAllCases() {
        List<FraudCaseResponse> cases = fraudCaseService.getAllCases()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cases);
    }

    /**
     * Açık fraud case'leri listeler
     * GET /fraud-cases/open
     */
    @GetMapping("/open")
    public ResponseEntity<List<FraudCaseResponse>> getOpenCases() {
        List<FraudCaseResponse> cases = fraudCaseService.getOpenCases()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cases);
    }

    /**
     * Case detayı getirir (aksiyon geçmişi ile)
     * GET /fraud-cases/{caseId}
     */
    @GetMapping("/{caseId}")
    public ResponseEntity<FraudCaseDetailResponse> getCaseDetails(@PathVariable String caseId) {
        FraudCase fraudCase = fraudCaseService.getCaseDetails(caseId);

        List<CaseActionResponse> actions = fraudCase.getHistory() != null
                ? fraudCase.getHistory().stream()
                    .map(a -> new CaseActionResponse(
                            a.getActionId(),
                            a.getActor(),
                            a.getActionType(),
                            a.getNote(),
                            a.getTimestamp()))
                    .collect(Collectors.toList())
                : List.of();

        FraudCaseDetailResponse response = new FraudCaseDetailResponse(
                fraudCase.getCaseId(),
                fraudCase.getUserId(),
                fraudCase.getStatus().name(),
                fraudCase.getPriority(),
                fraudCase.getCaseType(),
                fraudCase.getOpenedBy(),
                fraudCase.getOpenedAt(),
                fraudCase.getClosedAt(),
                actions
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Case durumunu günceller
     * PATCH /fraud-cases/{caseId}/status
     */
    @PatchMapping("/{caseId}/status")
    public ResponseEntity<FraudCaseResponse> updateCaseStatus(
            @PathVariable String caseId,
            @RequestBody UpdateCaseStatusRequest request) {

        fraudCaseService.updateCaseStatus(
                caseId,
                CaseStatus.valueOf(request.status()),
                request.actorName(),
                request.note()
        );

        FraudCase updatedCase = fraudCaseService.getCaseDetails(caseId);
        return ResponseEntity.ok(mapToResponse(updatedCase));
    }

    private FraudCaseResponse mapToResponse(FraudCase fraudCase) {
        return new FraudCaseResponse(
                fraudCase.getCaseId(),
                fraudCase.getUserId(),
                fraudCase.getStatus(),
                fraudCase.getPriority(),
                fraudCase.getOpenedAt(),
                fraudCase.getClosedAt()
        );
    }

    // Inner record classes for request/response
    public record UpdateCaseStatusRequest(
            String status,
            String actorName,
            String note
    ) {}

    public record FraudCaseDetailResponse(
            String caseId,
            String userId,
            String status,
            Integer priority,
            String caseType,
            String openedBy,
            java.time.LocalDateTime openedAt,
            java.time.LocalDateTime closedAt,
            List<CaseActionResponse> history
    ) {}
}