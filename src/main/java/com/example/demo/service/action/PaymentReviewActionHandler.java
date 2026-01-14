package com.example.demo.service.action;

import com.example.demo.dto.request.CreateFraudCaseRequest;
import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.IFraudCaseService;
import com.example.demo.service.INotificationService;
import org.springframework.stereotype.Component;

/**
 * Ödeme inceleme (review) handler'ı
 * SOLID: Single Responsibility - Sadece ödeme inceleme işaretlemesinden sorumlu
 * SOLID: Dependency Inversion - Concrete class yerine interface'e bağımlı
 */
@Component
public class PaymentReviewActionHandler implements ActionHandler {

    private final INotificationService notificationService;
    private final IFraudCaseService fraudCaseService;

    public PaymentReviewActionHandler(INotificationService notificationService,
                                       IFraudCaseService fraudCaseService) {
        this.notificationService = notificationService;
        this.fraudCaseService = fraudCaseService;
    }

    @Override
    public void handle(Event event) {
        // 1. Ödeme inceleme case'i oluştur (düşük öncelikli)
        fraudCaseService.createSystemCase(new CreateFraudCaseRequest(
                event.getUserId(),
                3, // Düşük öncelik - manuel inceleme
                "Ödeme incelemesi gerekli: Event " + event.getEventId() +
                ", Tutar: " + event.getAmount() + " " + event.getUnit()
        ));

        // 2. Kullanıcıya bildirim gönder
        String message = String.format(
                "Ödemeniz güvenlik kontrolü nedeniyle incelemeye alındı. " +
                "İşlem detayı: %s %s. En kısa sürede sonuçlandırılacaktır.",
                event.getAmount(),
                event.getUnit()
        );
        notificationService.sendBipNotification(event.getUserId(), message);

        System.out.println("Payment marked for review for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PAYMENT_REVIEW;
    }
}
