package com.example.demo.service;

import com.example.demo.dto.response.RiskProfileResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.RiskProfile;

import java.util.List;

/**
 * Risk Profile Service interface
 * Kullanıcı risk profili yönetimi
 */
public interface IRiskProfileService {

    /**
     * Kullanıcının risk profilini getirir
     * @param userId Kullanıcı ID
     * @return Risk profili response
     */
    RiskProfileResponse getRiskProfile(String userId);

    /**
     * Event'e göre risk profilini günceller
     * Risk skorunu hesaplar ve sinyalleri ekler
     * @param event İşlenen event
     * @return Güncellenmiş risk profili
     */
    RiskProfile updateRiskProfile(Event event);

    /**
     * Risk profiline sinyal ekler
     * @param userId Kullanıcı ID
     * @param signal Eklenecek sinyal
     */
    void addSignal(String userId, String signal);

    /**
     * Risk skorunu günceller
     * @param userId Kullanıcı ID
     * @param scoreDelta Skor değişimi (pozitif veya negatif)
     */
    void updateRiskScore(String userId, double scoreDelta);

    /**
     * Kullanıcının risk profilini sıfırlar
     * @param userId Kullanıcı ID
     */
    void resetRiskProfile(String userId);

    /**
     * Tüm risk profillerini getirir (dashboard için)
     * @return Risk profili listesi
     */
    List<RiskProfileResponse> getAllRiskProfiles();

    /**
     * Yüksek riskli kullanıcıları getirir
     * @return Yüksek riskli kullanıcı profilleri
     */
    List<RiskProfileResponse> getHighRiskProfiles();
}