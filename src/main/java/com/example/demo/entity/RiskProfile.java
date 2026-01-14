package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "risk_profile")
public class RiskProfile {
    //user_id, risk_score, risk_level, signals
    @Id
    private String userId;

    @Column(name = "risk_score")
    private Double riskScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @ElementCollection
    private List<String> signals;

    public RiskProfile() {
    }

    public RiskProfile(String userId, Double riskScore,
                       RiskLevel riskLevel, List<String> signals) {
        this.userId = userId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.signals = signals;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }
}
