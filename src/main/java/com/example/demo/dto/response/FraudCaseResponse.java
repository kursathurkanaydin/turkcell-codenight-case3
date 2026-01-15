package com.example.demo.dto.response;

import com.example.demo.entity.CaseStatus;

import java.time.LocalDateTime;

/**
 * Fraud Case response DTO
 * API response'ları için kullanılır
 * SOLID: Single Responsibility - Sadece data transfer'den sorumlu
 *
 * PDF gereksinimleri:
 * - case_type
 * - status (OPEN / IN_PROGRESS / CLOSED)
 * - priority (LOW/MEDIUM/HIGH/CRITICAL)
 * - opened_at
 * - opened_by (audit trail için)
 */
public record FraudCaseResponse(
        String caseId,
        String userId,
        CaseStatus status,
        String priority,  // PDF gereksinimi: LOW/MEDIUM/HIGH/CRITICAL
        String caseType,
        String openedBy,
        LocalDateTime openedAt,
        LocalDateTime closedAt
) {

    /**
     * Integer priority değerini String'e çevirir
     * 1 = CRITICAL, 2 = HIGH, 3 = MEDIUM, 4+ = LOW
     */
    public static String mapPriorityToString(Integer priority) {
        if (priority == null) return "LOW";
        return switch (priority) {
            case 1 -> "CRITICAL";
            case 2 -> "HIGH";
            case 3 -> "MEDIUM";
            default -> "LOW";
        };
    }
}