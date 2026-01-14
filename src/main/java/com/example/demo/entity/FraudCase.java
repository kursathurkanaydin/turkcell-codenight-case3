package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fraud_case")
public class FraudCase {
    //case_id, user_id, opened_by, case_type, status, opened_at, priority

    @Id
    private String caseId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "opened_by")
    private String openedBy;

    @Column(name = "case_type")
    private String caseType;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "priority")
    private Integer priority;

    @OneToMany(mappedBy = "fraudCase", cascade = CascadeType.ALL)
    private List<CaseAction> history;

    public FraudCase() {
    }

    public FraudCase(String caseId, String userId, String openedBy,
                     String caseType, CaseStatus status,
                     LocalDateTime openedAt, Integer priority,
                     List<CaseAction> history) {
        this.caseId = caseId;
        this.userId = userId;
        this.openedBy = openedBy;
        this.caseType = caseType;
        this.status = status;
        this.openedAt = openedAt;
        this.priority = priority;
        this.history = history;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<CaseAction> getHistory() {
        return history;
    }

    public void setHistory(List<CaseAction> history) {
        this.history = history;
    }
}
