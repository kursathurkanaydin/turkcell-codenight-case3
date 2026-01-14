package com.example.demo.service.model;

import com.example.demo.entity.ActionType;
import java.util.List;

public record ActionResult(ActionType selectedAction, List<ActionType> suppressedActions) {}