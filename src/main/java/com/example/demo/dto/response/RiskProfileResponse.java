package com.example.demo.dto.response;

import com.example.demo.entity.RiskLevel;

import java.util.List;

/**
 * Risk profili response DTO'su
 * PDF'deki örnek:
 * {
 *   "user_id": "U5",
 *   "risk_score": 90,
 *   "risk_level": "HIGH",
 *   "signals": ["high_ip_risk_new_device", "crypto_payment"]
 * }
 */
public record RiskProfileResponse(
        String userId,
        Double riskScore,
        RiskLevel riskLevel,
        List<String> signals
) {
}