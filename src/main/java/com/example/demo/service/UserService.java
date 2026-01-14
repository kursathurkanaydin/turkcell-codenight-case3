package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void blockUser(String userId) {
        // Kullanıcıyı engelleme işlemi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));
        user.setBlocked(true);
        userRepository.save(user);
        System.out.println("Kullanıcı engellendi: " + userId);
    }

    @Override
    @Transactional
    public void unblockUser(String userId) {
        // Kullanıcı engelini kaldırma işlemi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));
        user.setBlocked(false);
        userRepository.save(user);
        System.out.println("Kullanıcı engeli kaldırıldı: " + userId);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));
    }

    @Override
    public boolean isUserBlocked(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));
        return user.isBlocked();
    }
}
