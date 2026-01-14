package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "decision")
public class Decision {
    //decision_id, user_id, triggered_rules, selected_action, suppressed_actions, timestamp
    @Id
    private String decisionId;

    @Column(name = "user_id")
    private String userId;

    @ElementCollection
    private List<String> triggeredRules;

    @Enumerated(EnumType.STRING)
    private ActionType selectedAction;

    @ElementCollection
    private List<String> suppressedActions;

    @Column(name = "decision_timestamp")
    private LocalDateTime timestamp;

    public Decision() {
    }

    public Decision(String decisionId, String userId, List<String> triggeredRules, ActionType selectedAction, List<String> suppressedActions, LocalDateTime timestamp) {
        this.decisionId = decisionId;
        this.userId = userId;
        this.triggeredRules = triggeredRules;
        this.selectedAction = selectedAction;
        this.suppressedActions = suppressedActions;
        this.timestamp = timestamp;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(List<String> triggeredRules) {
        this.triggeredRules = triggeredRules;
    }

    public ActionType getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(ActionType selectedAction) {
        this.selectedAction = selectedAction;
    }

    public List<String> getSuppressedActions() {
        return suppressedActions;
    }

    public void setSuppressedActions(List<String> suppressedActions) {
        this.suppressedActions = suppressedActions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
