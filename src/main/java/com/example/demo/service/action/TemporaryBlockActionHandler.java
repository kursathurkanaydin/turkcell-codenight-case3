package com.example.demo.service.action;

import com.example.demo.entity.ActionType;
import com.example.demo.entity.Event;
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

    public TemporaryBlockActionHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Event event) {
        // Logic to temporarily block the user associated with the event
        // Kullanıcı hesabını geçici olarak engelleme işlemi yapılabilir
        userService.blockUser(event.getUserId());
        System.out.println("Temporarily blocking user for event: " + event.getEventId());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TEMPORARY_BLOCK;
    }
}