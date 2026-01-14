package com.example.demo.service;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.action.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Action orchestrator service
 * SOLID Principles uygulanmış:
 * - Single Responsibility: Sadece doğru handler'ı bulmak ve çağırmaktan sorumlu
 * - Open/Closed: Yeni action type eklendiğinde bu sınıf değişmez, sadece yeni handler eklenir
 * - Dependency Inversion: Concrete class'lar yerine ActionHandler interface'ine bağımlı
 */
@Service
public class ActionService {

    private final Map<ActionType, ActionHandler> handlers;

    /**
     * Constructor injection ile tüm ActionHandler implementasyonlarını alır
     * Spring otomatik olarak tüm @Component ActionHandler'ları inject eder
     * @param handlerList Spring tarafından inject edilen tüm ActionHandler'lar
     */
    public ActionService(List<ActionHandler> handlerList) {
        // Her ActionType için ilgili handler'ı Map'e koyar
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        ActionHandler::getActionType,
                        Function.identity()
                ));
    }

    /**
     * Verilen action type için ilgili handler'ı bulup çalıştırır
     * @param type Action type
     * @param event İşlenecek event
     */
    public void performAction(ActionType type, Event event) {
        ActionHandler handler = handlers.get(type);

        if (handler == null) {
            throw new IllegalArgumentException("Unknown action type: " + type);
        }

        handler.handle(event);
    }
}
