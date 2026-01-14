package com.example.demo.dto.response;

import com.example.demo.entity.ActionType;

/**
 * Risk kuralı response DTO'su
 */
public record RiskRuleResponse(
        String ruleId,
        String condition,
        ActionType action,
        Integer priority,
        Boolean isActive
) {
}