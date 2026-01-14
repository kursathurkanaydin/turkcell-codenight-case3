package com.example.demo.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record EventIngestRequest(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("user_id") String userId,
        String service,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("value") Double value,
        String unit,
        String meta,
        Instant timestamp
) {}