package com.example.demo.service;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    private final NotificationService notificationService;
    private final UserService userService;
    private final FraudCaseService fraudCaseService;
    public ActionService(NotificationService notificationService, UserService userService, FraudCaseService fraudCaseService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.fraudCaseService = fraudCaseService;
    }

    public void performAction(ActionType type, Event event) {
        switch (type) {
            case FORCE_2FA:
                handleForce2FA(event);
                break;
            case OPEN_FRAUD_CASE:
                handleOpenFraudCase(event);
                break;
            case TEMPORARY_BLOCK:
                handleTemporaryBlock(event);
                break;
            case PAYMENT_REVIEW:
                handleReview(event);
                break;
            default:
                throw new IllegalArgumentException("Unknown action type: " + type);
        }
    }

    private void handleForce2FA(Event event) {
        // Logic to enforce 2FA for the user associated with the event
        //Notification service ile kullanıcıya 2FA zorunluluğu bildirilebilir
        notificationService.sendBipNotification(event.getUserId(), "Lütfen hesabınıza giriş yaparken 2FA doğrulamasını tamamlayın.");
        System.out.println("Enforcing 2FA for event: " + event.getEventId());
    }

    private void handleOpenFraudCase(Event event) {
        // Logic to open a fraud case for the event
        // Fraud management system ile entegre edilebilir
        fraudCaseService.createSystemCase(event.getUserId(), 1, "Otomatik olarak oluşturulan fraud case.");
        System.out.println("Opening fraud case for event: " + event.getEventId());
    }

    private void handleTemporaryBlock(Event event) {
        // Logic to temporarily block the user associated with the event
        //Kullanıcı hesabını geçici olarak engelleme işlemi yapılabilir
        userService.blockUser(event.getUserId());
        System.out.println("Temporarily blocking user for event: " + event.getEventId());
    }

    private void handleReview(Event event) {
        // Logic to mark the payment for manual review
        //Ödeme inceleme ekibine yönlendirilebilir
        System.out.println("Marking payment for review for event: " + event.getEventId());
    }
}
