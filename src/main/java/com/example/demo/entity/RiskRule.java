package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_rule")
public class RiskRule {
    //rule_id, condition, action, priority, is_active
    @Id
    private String ruleId;

    @Column(name = "condition",columnDefinition = "TEXT")
    private String condition;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "is_active")
    private Boolean isActive;

    public RiskRule() {
    }

    public RiskRule(String ruleId, String condition, ActionType action,
                    Integer priority, Boolean isActive) {
        this.ruleId = ruleId;
        this.condition = condition;
        this.action = action;
        this.priority = priority;
        this.isActive = isActive;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
