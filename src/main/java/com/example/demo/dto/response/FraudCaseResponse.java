package com.example.demo.dto.response;

import com.example.demo.entity.CaseStatus;

import java.time.LocalDateTime;

/**
 * Fraud Case response DTO
 * API response'ları için kullanılır
 * SOLID: Single Responsibility - Sadece data transfer'den sorumlu
 */
public record FraudCaseResponse(
        String caseId,
        String userId,
        CaseStatus status,
        Integer priority,
        LocalDateTime openedAt,
        LocalDateTime closedAt
) {
}