package com.example.demo.dto.response;

import com.example.demo.entity.ActionType;

public record RiskRuleResponse(
        String id,
        String condition,
        ActionType addition,
        Integer priority,
        Boolean active
) {
}
