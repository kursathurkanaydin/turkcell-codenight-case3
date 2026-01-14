package com.example.demo.service.action;

import com.example.demo.dto.request.CreateFraudCaseRequest;
import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.IFraudCaseService;
import org.springframework.stereotype.Component;

/**
 * Fraud case açma handler'ı
 * SOLID: Single Responsibility - Sadece fraud case oluşturmaktan sorumlu
 * SOLID: Dependency Inversion - Concrete class yerine interface'e bağımlı
 */
@Component
public class OpenFraudCaseActionHandler implements ActionHandler {

    private final IFraudCaseService fraudCaseService;

    public OpenFraudCaseActionHandler(IFraudCaseService fraudCaseService) {
        this.fraudCaseService = fraudCaseService;
    }

    @Override
    public void handle(Event event) {
        // Logic to open a fraud case for the event
        // Fraud management system ile entegre edilebilir
        fraudCaseService.createSystemCase(new CreateFraudCaseRequest(
                event.getUserId(),
                1,
                "Otomatik olarak oluşturulan fraud case for event: " + event.getEventId()
        ));
        System.out.println("Opening fraud case for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.OPEN_FRAUD_CASE;
    }
}