package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import org.springframework.stereotype.Component;

/**
 * Ödeme inceleme (review) handler'ı
 * SOLID: Single Responsibility - Sadece ödeme inceleme işaretlemesinden sorumlu
 */
@Component
public class PaymentReviewActionHandler implements ActionHandler {

    @Override
    public void handle(Event event) {
        // Logic to mark the payment for manual review
        // Ödeme inceleme ekibine yönlendirilebilir
        System.out.println("Marking payment for review for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PAYMENT_REVIEW;
    }
}
