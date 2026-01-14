package com.example.demo.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "bip_notification")
public class BipNotification {
    //notification_id, user_id, channel, message, sent_at
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "channel")
    private String channel;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public BipNotification() {
    }

    public BipNotification(String notificationId, String userId, String channel,
                           String message, LocalDateTime sentAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.channel = channel;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
