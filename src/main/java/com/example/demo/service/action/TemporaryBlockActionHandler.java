package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
import com.example.demo.service.INotificationService;
import com.example.demo.service.IUserService;
import org.springframework.stereotype.Component;

/**
 * Kullanıcı geçici engelleme handler'ı
 * SOLID: Single Responsibility - Sadece kullanıcı engelleme işleminden sorumlu
 * SOLID: Dependency Inversion - Concrete class yerine interface'e bağımlı
 */
@Component
public class TemporaryBlockActionHandler implements ActionHandler {

    private final IUserService userService;
    private final INotificationService notificationService;

    public TemporaryBlockActionHandler(IUserService userService,
                                        INotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Event event) {

        // 1. Kullanıcı hesabını geçici olarak engelle
        userService.blockUser(event.getUserId());

        // 2. Kullanıcıya bildirim gönder
        String message = String.format(
                "Güvenlik nedeniyle hesabınız geçici olarak askıya alınmıştır. " +
                "Detaylı bilgi için müşteri hizmetleri ile iletişime geçiniz. " +
                "Referans: %s",
                event.getEventId()
        );
        notificationService.sendBipNotification(event.getUserId(), message);

        System.out.println("Temporarily blocking user for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TEMPORARY_BLOCK;
    }
}