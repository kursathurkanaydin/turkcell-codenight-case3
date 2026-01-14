package com.example.demo.service;

import com.example.demo.dto.response.DecisionResponse;
import com.example.demo.entity.ActionType;
import com.example.demo.entity.Decision;
import com.example.demo.entity.RiskRule;
import com.example.demo.repository.DecisionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decision Service implementasyonu
 * Karar kayıt ve yönetimi (Audit Log)
 *
 * PDF'deki gereksinim:
 * {
 *   "decision_id": "D-2003",
 *   "user_id": "U5",
 *   "triggered_rules": ["RR-01", "RR-02"],
 *   "selected_action": "FORCE_2FA",
 *   "suppressed_actions": ["PAYMENT_REVIEW"],
 *   "timestamp": "2026-03-12T19:09:00Z"
 * }
 */
@Service
public class DecisionService implements IDecisionService {

    private final DecisionRepository decisionRepository;

    public DecisionService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    @Override
    @Transactional
    public Decision recordDecision(String userId,
                                   List<RiskRule> triggeredRules,
                                   ActionType selectedAction,
                                   List<ActionType> suppressedActions) {
        Decision decision = new Decision();
        decision.setUserId(userId);
        decision.setTimestamp(LocalDateTime.now());

        // Tetiklenen kural ID'lerini çıkar
        List<String> ruleIds = triggeredRules.stream()
                .map(RiskRule::getRuleId)
                .collect(Collectors.toList());
        decision.setTriggeredRules(ruleIds);

        // Seçilen aksiyon
        decision.setSelectedAction(selectedAction);

        // Bastırılan aksiyonları string olarak kaydet
        List<String> suppressedActionStrings = suppressedActions.stream()
                .map(ActionType::name)
                .collect(Collectors.toList());
        decision.setSuppressedActions(suppressedActionStrings);

        Decision saved = decisionRepository.save(decision);

        System.out.println(String.format(
                "Decision kaydedildi: User=%s, TriggeredRules=%s, SelectedAction=%s, Suppressed=%s",
                userId, ruleIds, selectedAction, suppressedActionStrings));

        return saved;
    }

    @Override
    public List<DecisionResponse> getAllDecisions() {
        return decisionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DecisionResponse> getDecisionsByUserId(String userId) {
        return decisionRepository.findAll()
                .stream()
                .filter(d -> d.getUserId().equals(userId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DecisionResponse> getRecentDecisions(int limit) {
        return decisionRepository.findAll()
                .stream()
                .sorted((d1, d2) -> d2.getTimestamp().compareTo(d1.getTimestamp()))
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DecisionResponse getDecisionById(String decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision bulunamadı: " + decisionId));
        return mapToResponse(decision);
    }

    private DecisionResponse mapToResponse(Decision decision) {
        return new DecisionResponse(
                decision.getDecisionId(),
                decision.getUserId(),
                decision.getTriggeredRules(),
                decision.getSelectedAction(),
                decision.getSuppressedActions(),
                decision.getTimestamp()
        );
    }
}