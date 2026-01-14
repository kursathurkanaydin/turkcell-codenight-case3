package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.INotificationService;
import org.springframework.stereotype.Component;

/**
 * 2FA zorunluluğu bildirimi için handler
 * SOLID: Single Responsibility - Sadece 2FA notification göndermekten sorumlu
 * SOLID: Dependency Inversion - Concrete class yerine interface'e bağımlı
 */
@Component
public class Force2FAActionHandler implements ActionHandler {

    private final INotificationService notificationService;

    public Force2FAActionHandler(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Event event) {
        // Logic to enforce 2FA for the user associated with the event
        // Notification service ile kullanıcıya 2FA zorunluluğu bildirilebilir
        notificationService.sendBipNotification(
                event.getUserId(),
                "Lütfen hesabınıza giriş yaparken 2FA doğrulamasını tamamlayın."
        );
        System.out.println("Enforcing 2FA for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.FORCE_2FA;
    }
}