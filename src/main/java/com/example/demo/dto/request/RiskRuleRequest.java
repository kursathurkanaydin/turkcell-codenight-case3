package com.example.demo.dto.request;

import com.example.demo.entity.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Risk kuralı oluşturma/güncelleme request DTO'su
 * PDF'deki örnek:
 * {
 *   "rule_id": "RR-01",
 *   "condition": "BiP yeni cihaz + ip_risk=high",
 *   "action": "FORCE_2FA",
 *   "priority": 1,
 *   "is_active": true
 * }
 */
public record RiskRuleRequest(
        String ruleId,  // Opsiyonel - boşsa sistem üretir

        @NotBlank(message = "Koşul zorunludur")
        String condition,

        @NotNull(message = "Aksiyon zorunludur")
        ActionType action,

        @NotNull(message = "Öncelik zorunludur")
        Integer priority,

        Boolean isActive  // Default true
) {
}