package com.example.demo.dto.response;

import com.example.demo.entity.ActionType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Decision/Karar response DTO'su
 * PDF'deki örnek:
 * {
 *   "decision_id": "D-2003",
 *   "user_id": "U5",
 *   "triggered_rules": ["RR-01", "RR-02"],
 *   "selected_action": "FORCE_2FA",
 *   "suppressed_actions": ["PAYMENT_REVIEW"],
 *   "timestamp": "2026-03-12T19:09:00Z"
 * }
 */
public record DecisionResponse(
        String decisionId,
        String userId,
        List<String> triggeredRules,
        ActionType selectedAction,
        List<String> suppressedActions,
        LocalDateTime timestamp
) {
}