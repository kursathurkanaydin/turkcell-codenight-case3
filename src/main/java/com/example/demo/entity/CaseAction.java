package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_actions")
public class CaseAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String actionId; // [cite: 120]

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private FraudCase fraudCase; // [cite: 120]

    // Yapılan işlemin türü (Örn: "STATUS_CHANGE", "COMMENT_ADDED", "CASE_OPENED")
    private String actionType; // [cite: 120]

    // İşlemi yapan kişi veya sistem (Örn: "System", "Ahmet.Yilmaz")
    private String actor; // [cite: 120]

    // İşlemle ilgili açıklama veya not
    @Column(columnDefinition = "TEXT")
    private String note; // [cite: 120]

    @Column(name = "event_timestamp")
    private LocalDateTime timestamp; // [cite: 120]

    // --- Constructor, Getters & Setters ---

    // Boş Constructor (JPA için gereklidir)
    public CaseAction() {}

    // Kolay oluşturma için Constructor
    public CaseAction(FraudCase fraudCase, String actionType, String actor, String note) {
        this.fraudCase = fraudCase;
        this.actionType = actionType;
        this.actor = actor;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }

    public FraudCase getFraudCase() { return fraudCase; }
    public void setFraudCase(FraudCase fraudCase) { this.fraudCase = fraudCase; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}