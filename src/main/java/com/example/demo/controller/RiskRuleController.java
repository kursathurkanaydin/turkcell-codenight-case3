package com.example.demo.controller;

import com.example.demo.dto.request.RiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.service.IRiskRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Risk Rule Controller
 * PDF Bonus Özellik: Risk Rule Yönetim Ekranı
 *
 * GET /risk-rules - Tüm kuralları listeler
 * GET /risk-rules/active - Aktif kuralları listeler
 * GET /risk-rules/{ruleId} - Kural detayı getirir
 * POST /risk-rules - Yeni kural oluşturur
 * PUT /risk-rules/{ruleId} - Kuralı günceller
 * PATCH /risk-rules/{ruleId}/activate - Kuralı aktif yapar
 * PATCH /risk-rules/{ruleId}/deactivate - Kuralı pasif yapar
 * DELETE /risk-rules/{ruleId} - Kuralı siler
 */
@RestController
@RequestMapping("/risk-rules")
public class RiskRuleController {

    private final IRiskRuleService riskRuleService;

    public RiskRuleController(IRiskRuleService riskRuleService) {
        this.riskRuleService = riskRuleService;
    }

    /**
     * Tüm kuralları listeler
     * GET /risk-rules
     */
    @GetMapping
    public ResponseEntity<List<RiskRuleResponse>> getAllRules() {
        List<RiskRuleResponse> rules = riskRuleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    /**
     * Aktif kuralları listeler
     * GET /risk-rules/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<RiskRuleResponse>> getActiveRules() {
        List<RiskRuleResponse> rules = riskRuleService.getActiveRules();
        return ResponseEntity.ok(rules);
    }

    /**
     * Kural detayı getirir
     * GET /risk-rules/{ruleId}
     */
    @GetMapping("/{ruleId}")
    public ResponseEntity<RiskRuleResponse> getRuleById(@PathVariable String ruleId) {
        RiskRuleResponse rule = riskRuleService.getRuleById(ruleId);
        return ResponseEntity.ok(rule);
    }

    /**
     * Yeni kural oluşturur
     * POST /risk-rules
     */
    @PostMapping
    public ResponseEntity<RiskRuleResponse> createRule(@Valid @RequestBody RiskRuleRequest request) {
        RiskRuleResponse rule = riskRuleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    /**
     * Kuralı günceller
     * PUT /risk-rules/{ruleId}
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<RiskRuleResponse> updateRule(
            @PathVariable String ruleId,
            @Valid @RequestBody RiskRuleRequest request) {
        RiskRuleResponse rule = riskRuleService.updateRule(ruleId, request);
        return ResponseEntity.ok(rule);
    }

    /**
     * Kuralı aktif yapar
     * PATCH /risk-rules/{ruleId}/activate
     */
    @PatchMapping("/{ruleId}/activate")
    public ResponseEntity<RiskRuleResponse> activateRule(@PathVariable String ruleId) {
        RiskRuleResponse rule = riskRuleService.setRuleActive(ruleId, true);
        return ResponseEntity.ok(rule);
    }

    /**
     * Kuralı pasif yapar
     * PATCH /risk-rules/{ruleId}/deactivate
     */
    @PatchMapping("/{ruleId}/deactivate")
    public ResponseEntity<RiskRuleResponse> deactivateRule(@PathVariable String ruleId) {
        RiskRuleResponse rule = riskRuleService.setRuleActive(ruleId, false);
        return ResponseEntity.ok(rule);
    }

    /**
     * Kuralı siler
     * DELETE /risk-rules/{ruleId}
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable String ruleId) {
        riskRuleService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }
}