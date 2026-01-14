package com.example.demo.controller;

import com.example.demo.dto.response.RiskProfileResponse;
import com.example.demo.service.IRiskProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Risk Profile Controller
 * GET /users/{id}/risk-profile - Kullanıcının risk profilini getirir
 * GET /risk-profiles - Tüm risk profillerini listeler
 * GET /risk-profiles/high-risk - Yüksek riskli kullanıcıları listeler
 * POST /users/{id}/risk-profile/reset - Risk profilini sıfırlar
 */
@RestController
public class RiskProfileController {

    private final IRiskProfileService riskProfileService;

    public RiskProfileController(IRiskProfileService riskProfileService) {
        this.riskProfileService = riskProfileService;
    }

    /**
     * Kullanıcının risk profilini getirir
     * GET /users/{id}/risk-profile
     */
    @GetMapping("/users/{id}/risk-profile")
    public ResponseEntity<RiskProfileResponse> getRiskProfile(@PathVariable String id) {
        RiskProfileResponse profile = riskProfileService.getRiskProfile(id);
        return ResponseEntity.ok(profile);
    }

    /**
     * Tüm risk profillerini listeler
     * GET /risk-profiles
     */
    @GetMapping("/risk-profiles")
    public ResponseEntity<List<RiskProfileResponse>> getAllRiskProfiles() {
        List<RiskProfileResponse> profiles = riskProfileService.getAllRiskProfiles();
        return ResponseEntity.ok(profiles);
    }

    /**
     * Yüksek riskli kullanıcıları listeler
     * GET /risk-profiles/high-risk
     */
    @GetMapping("/risk-profiles/high-risk")
    public ResponseEntity<List<RiskProfileResponse>> getHighRiskProfiles() {
        List<RiskProfileResponse> profiles = riskProfileService.getHighRiskProfiles();
        return ResponseEntity.ok(profiles);
    }

    /**
     * Kullanıcının risk profilini sıfırlar
     * POST /users/{id}/risk-profile/reset
     */
    @PostMapping("/users/{id}/risk-profile/reset")
    public ResponseEntity<Void> resetRiskProfile(@PathVariable String id) {
        riskProfileService.resetRiskProfile(id);
        return ResponseEntity.ok().build();
    }
}