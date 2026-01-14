package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * Case Action response DTO
 * Case geçmişi için kullanılır
 * SOLID: Single Responsibility - Sadece data transfer'den sorumlu
 */
public record CaseActionResponse(
        String actionId,
        String actor,
        String actionType,
        String note,
        LocalDateTime timestamp
) {
}