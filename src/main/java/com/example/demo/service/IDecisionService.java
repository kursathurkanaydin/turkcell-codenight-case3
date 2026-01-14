package com.example.demo.service;

import com.example.demo.dto.response.DecisionResponse;
import com.example.demo.entity.ActionType;
import com.example.demo.entity.Decision;
import com.example.demo.entity.RiskRule;

import java.util.List;

/**
 * Decision Service interface
 * Karar kayıt ve yönetimi (Audit Log)
 */
public interface IDecisionService {

    /**
     * Yeni karar kaydeder
     * @param userId Kullanıcı ID
     * @param triggeredRules Tetiklenen kurallar
     * @param selectedAction Seçilen aksiyon
     * @param suppressedActions Bastırılan aksiyonlar
     * @return Kaydedilen karar
     */
    Decision recordDecision(String userId,
                            List<RiskRule> triggeredRules,
                            ActionType selectedAction,
                            List<ActionType> suppressedActions);

    /**
     * Tüm kararları getirir
     * @return Karar listesi
     */
    List<DecisionResponse> getAllDecisions();

    /**
     * Kullanıcının kararlarını getirir
     * @param userId Kullanıcı ID
     * @return Karar listesi
     */
    List<DecisionResponse> getDecisionsByUserId(String userId);

    /**
     * Son kararları getirir (dashboard için)
     * @param limit Limit
     * @return Son kararlar
     */
    List<DecisionResponse> getRecentDecisions(int limit);

    /**
     * Karar ID'ye göre karar getirir
     * @param decisionId Karar ID
     * @return Karar
     */
    DecisionResponse getDecisionById(String decisionId);
}