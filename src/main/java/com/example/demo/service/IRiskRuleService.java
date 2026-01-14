package com.example.demo.service;

import com.example.demo.dto.request.RiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.RiskProfile;
import com.example.demo.entity.RiskRule;

import java.util.List;

/**
 * Risk Rule Service interface
 * Risk kuralları yönetimi ve değerlendirmesi
 */
public interface IRiskRuleService {

    /**
     * Tüm aktif kuralları getirir
     * @return Aktif kural listesi
     */
    List<RiskRuleResponse> getActiveRules();

    /**
     * Tüm kuralları getirir
     * @return Tüm kurallar
     */
    List<RiskRuleResponse> getAllRules();

    /**
     * Kural ID'ye göre kural getirir
     * @param ruleId Kural ID
     * @return Kural
     */
    RiskRuleResponse getRuleById(String ruleId);

    /**
     * Yeni kural oluşturur
     * @param request Kural request
     * @return Oluşturulan kural
     */
    RiskRuleResponse createRule(RiskRuleRequest request);

    /**
     * Kuralı günceller
     * @param ruleId Kural ID
     * @param request Güncellenecek kural bilgileri
     * @return Güncellenen kural
     */
    RiskRuleResponse updateRule(String ruleId, RiskRuleRequest request);

    /**
     * Kuralı aktif/pasif yapar
     * @param ruleId Kural ID
     * @param isActive Aktiflik durumu
     * @return Güncellenen kural
     */
    RiskRuleResponse setRuleActive(String ruleId, boolean isActive);

    /**
     * Kuralı siler
     * @param ruleId Kural ID
     */
    void deleteRule(String ruleId);

    /**
     * Event ve risk profiline göre tetiklenen kuralları değerlendirir
     * @param event İşlenen event
     * @param riskProfile Kullanıcının risk profili
     * @return Tetiklenen kurallar (önceliğe göre sıralı)
     */
    List<RiskRule> evaluateRules(Event event, RiskProfile riskProfile);
}