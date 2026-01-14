package com.example.demo.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Dashboard özet response DTO'su
 * PDF'deki gereksinimler:
 * - Son gelen event'ler
 * - Kullanıcı risk seviyeleri ve skorları
 * - Açık fraud case'ler ve durumları
 * - Tetiklenen kurallar ve aksiyonlar
 * - Karar logları
 */
public record DashboardSummaryResponse(
        // Son event'ler
        List<EventResponse> recentEvents,

        // Risk profil özeti
        long totalUsers,
        long highRiskUsers,
        long mediumRiskUsers,
        long lowRiskUsers,

        // Fraud case özeti
        long openCases,
        long inProgressCases,
        long closedCases,

        // Son kararlar
        List<DecisionResponse> recentDecisions,

        // Kural tetiklenme istatistikleri
        Map<String, Long> ruleTriggeredCounts,

        // Aksiyon istatistikleri
        Map<String, Long> actionExecutedCounts
) {
}