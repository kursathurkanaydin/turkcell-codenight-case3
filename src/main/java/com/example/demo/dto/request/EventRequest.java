package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Event oluşturma request DTO'su
 * PDF'deki örnek:
 * {
 *   "event_id": "EV-5002",
 *   "user_id": "U5",
 *   "service": "Paycell",
 *   "event_type": "PAYMENT",
 *   "value": 950,
 *   "unit": "TRY",
 *   "meta": "merchant=CryptoExchange",
 *   "timestamp": "2026-03-12T19:06:00Z"
 * }
 */
public record EventRequest(
        String eventId,  // Opsiyonel - boşsa sistem üretir

        @NotBlank(message = "User ID zorunludur")
        String userId,

        @NotBlank(message = "Servis bilgisi zorunludur")
        String service,

        @NotBlank(message = "Event tipi zorunludur")
        String eventType,

        @NotNull(message = "Değer zorunludur")
        Double value,

        String unit,

        String meta  // Opsiyonel meta bilgisi (cihaz, ip_risk, merchant vs.)
) {
}