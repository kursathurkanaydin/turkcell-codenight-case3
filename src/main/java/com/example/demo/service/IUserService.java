package com.example.demo.service;

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
}