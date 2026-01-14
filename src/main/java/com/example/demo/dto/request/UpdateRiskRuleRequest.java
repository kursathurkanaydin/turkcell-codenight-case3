package com.example.demo.dto.request;

import com.example.demo.entity.ActionType;

public record UpdateRiskRuleRequest(
        String id,
        String condition,
        ActionType action,
        Integer priority,
        Boolean active
){
}
