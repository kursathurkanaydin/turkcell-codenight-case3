package com.example.demo.service;

/**
 * Notification Service interface
 * SOLID: Dependency Inversion - Concrete implementasyon yerine interface'e bağımlılık
 * SOLID: Interface Segregation - Sadece gerekli metodları içerir
 */
public interface INotificationService {

    /**
     * BiP üzerinden bildirim gönderir
     * @param userId Kullanıcı ID
     * @param message Mesaj
     */
    void sendBipNotification(String userId, String message);
}