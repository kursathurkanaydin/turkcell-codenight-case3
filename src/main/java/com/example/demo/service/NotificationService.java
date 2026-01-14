package com.example.demo.service;

import com.example.demo.entity.BipNotification;
import com.example.demo.repository.BipNotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService implements INotificationService {

    private final BipNotificationRepository notificationRepository; // DB'ye yazmak için

    public NotificationService(BipNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void sendBipNotification(String userId, String message) {
        // 1. Simülasyon: Konsola yaz (Gerçekte HTTP isteği atılırdı)
        System.out.println("BiP Mesajı Gönderiliyor -> User: " + userId + " | Msg: " + message);

        // 2. Kayıt: 'bip_notifications' tablosuna log at [cite: 123]
        BipNotification notif = new BipNotification();
        notif.setUserId(userId);
        notif.setMessage(message);
        notif.setChannel("BIP");
        notif.setSentAt(LocalDateTime.now());

        notificationRepository.save(notif);
    }
}