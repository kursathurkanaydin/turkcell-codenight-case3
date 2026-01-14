package com.example.demo.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class Event {
    //event_id, user_id, service, event_type, value, unit, meta, timestamp
    @Id
    private String eventId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "service")
    private String service;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "unit")
    private String unit;

    @Column(name = "meta")
    private String meta;

    @Column(name = "event_timestamp")
    private LocalDateTime timestamp;

    public Event() {
    }

    public Event(String eventId, String userId, String service, String eventType,
                 Double amount, String unit, String meta, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.userId = userId;
        this.service = service;
        this.eventType = eventType;
        this.amount = amount;
        this.unit = unit;
        this.meta = meta;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
