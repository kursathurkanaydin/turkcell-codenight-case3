package com.example.demo.service;

import com.example.demo.entity.User;

/**
 * User Service interface
 * SOLID: Dependency Inversion - Concrete implementasyon yerine interface'e bağımlılık
 * SOLID: Interface Segregation - Sadece gerekli metodları içerir
 */
public interface IUserService {

    /**
     * Kullanıcıyı engeller (bloklar)
     * @param userId Kullanıcı ID
     */
    void blockUser(String userId);

    /**
     * Kullanıcının engelini kaldırır
     * @param userId Kullanıcı ID
     */
    void unblockUser(String userId);

    /**
     * Kullanıcı bilgilerini getirir
     * @param userId Kullanıcı ID
     * @return User entity
     */
    User getUserById(String userId);

    /**
     * Kullanıcının engellenip engellenmediğini kontrol eder
     * @param userId Kullanıcı ID
     * @return Engellenmiş ise true
     */
    boolean isUserBlocked(String userId);
}