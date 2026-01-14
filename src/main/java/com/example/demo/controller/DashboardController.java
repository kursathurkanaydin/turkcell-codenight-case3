package com.example.demo.controller;

import com.example.demo.dto.response.DashboardSummaryResponse;
import com.example.demo.service.IRiskEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard Controller
 * GET /dashboard/summary - Dashboard özeti getirir
 *
 * PDF'deki gereksinimler:
 * - Son gelen event'ler
 * - Kullanıcı risk seviyeleri ve skorları
 * - Açık fraud case'ler ve durumları
 * - Tetiklenen kurallar ve aksiyonlar
 * - Karar logları
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final IRiskEngineService riskEngineService;

    public DashboardController(IRiskEngineService riskEngineService) {
        this.riskEngineService = riskEngineService;
    }

    /**
     * Dashboard özeti getirir
     * GET /dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = riskEngineService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}