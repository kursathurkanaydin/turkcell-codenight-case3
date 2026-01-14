package com.example.demo.repository;

import com.example.demo.entity.BipNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BipNotificationRepository extends JpaRepository<BipNotification, String> {
}
