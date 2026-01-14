package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * Event response DTO'su
 */
public record EventResponse(
        String eventId,
        String userId,
        String service,
        String eventType,
        Double amount,
        String unit,
        String meta,
        LocalDateTime timestamp
) {
}