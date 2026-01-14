package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.INotificationService;
import org.springframework.stereotype.Component;

/**
 * Anomali uyarısı handler'ı
 * SOLID: Single Responsibility - Sadece anomali alert bildirimi göndermekten sorumlu
 * SOLID: Dependency Inversion - Concrete class yerine interface'e bağımlı
 */
@Component
public class AnomalyAlertActionHandler implements ActionHandler {

    private final INotificationService notificationService;

    public AnomalyAlertActionHandler(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Event event) {
        // Anomali tespit edildiğinde kullanıcıya ve güvenlik ekibine bildirim gönder
        String message = String.format(
                "Hesabınızda anormal bir aktivite tespit edildi. Event: %s, Servis: %s, Tip: %s",
                event.getEventId(),
                event.getService(),
                event.getEventType()
        );

        notificationService.sendBipNotification(event.getUserId(), message);
        System.out.println("Anomaly alert sent for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ANOMALY_ALERT;
    }
}