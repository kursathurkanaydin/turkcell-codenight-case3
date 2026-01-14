package com.example.demo.dto.request;

public record CreateFraudCaseRequest(
        String userId,
        Integer priority,
        String ruleDescription
) {
}
