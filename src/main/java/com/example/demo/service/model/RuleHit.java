package com.example.demo.service.model;

import com.example.demo.entity.ActionType;

public record RuleHit(String ruleId, int priority, ActionType action) {}