package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;

/**
 * Strategy Pattern için base interface
 * Her action type bu interface'i implement eder
 * SOLID: Single Responsibility - Her handler sadece kendi action'ını handle eder
 */
public interface ActionHandler {

    /**
     * Action'ı işler
     * @param event İşlenecek event
     */
    void handle(Event event);

    /**
     * Bu handler'ın handle ettiği action type'ı döner
     * @return ActionType
     */
    ActionType getActionType();
}