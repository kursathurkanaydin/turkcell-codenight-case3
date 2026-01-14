package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void blockUser(String userId) {
        // Kullanıcıyı engelleme işlemi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));
        user.setBlocked(true);
        userRepository.save(user);
        System.out.println("Kullanıcı engellendi: " + userId);
    }
}
