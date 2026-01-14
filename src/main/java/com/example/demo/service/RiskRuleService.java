package com.example.demo.service;

import com.example.demo.dto.request.RiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.RiskLevel;
import com.example.demo.entity.RiskProfile;
import com.example.demo.entity.RiskRule;
import com.example.demo.repository.RiskRuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Risk Rule Service implementasyonu
 * Risk kurallarını yönetir ve değerlendirir
 *
 * Kural condition formatı örnekleri:
 * - "service=Paycell AND amount>1000"
 * - "risk_level=HIGH"
 * - "signal=crypto_payment"
 * - "event_type=PAYMENT AND meta contains crypto"
 * - "ip_risk=high AND new_device=true"
 */
@Service
public class RiskRuleService implements IRiskRuleService {

    private final RiskRuleRepository riskRuleRepository;

    public RiskRuleService(RiskRuleRepository riskRuleRepository) {
        this.riskRuleRepository = riskRuleRepository;
    }

    @Override
    public List<RiskRuleResponse> getActiveRules() {
        return riskRuleRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskRuleResponse> getAllRules() {
        return riskRuleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RiskRuleResponse getRuleById(String ruleId) {
        RiskRule rule = riskRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Kural bulunamadı: " + ruleId));
        return mapToResponse(rule);
    }

    @Override
    @Transactional
    public RiskRuleResponse createRule(RiskRuleRequest request) {
        RiskRule rule = new RiskRule();
        rule.setRuleId(request.ruleId() != null && !request.ruleId().isBlank()
                ? request.ruleId()
                : "RR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        rule.setCondition(request.condition());
        rule.setAction(request.action());
        rule.setPriority(request.priority());
        rule.setActive(request.isActive() != null ? request.isActive() : true);

        RiskRule saved = riskRuleRepository.save(rule);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public RiskRuleResponse updateRule(String ruleId, RiskRuleRequest request) {
        RiskRule rule = riskRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Kural bulunamadı: " + ruleId));

        rule.setCondition(request.condition());
        rule.setAction(request.action());
        rule.setPriority(request.priority());
        if (request.isActive() != null) {
            rule.setActive(request.isActive());
        }

        RiskRule saved = riskRuleRepository.save(rule);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public RiskRuleResponse setRuleActive(String ruleId, boolean isActive) {
        RiskRule rule = riskRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Kural bulunamadı: " + ruleId));

        rule.setActive(isActive);
        RiskRule saved = riskRuleRepository.save(rule);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRule(String ruleId) {
        if (!riskRuleRepository.existsById(ruleId)) {
            throw new IllegalArgumentException("Kural bulunamadı: " + ruleId);
        }
        riskRuleRepository.deleteById(ruleId);
    }

    @Override
    public List<RiskRule> evaluateRules(Event event, RiskProfile riskProfile) {
        // Tüm aktif kuralları al
        List<RiskRule> activeRules = riskRuleRepository.findByIsActiveTrue();

        // Her kuralı değerlendir ve tetiklenenleri topla
        return activeRules.stream()
                .filter(rule -> evaluateCondition(rule.getCondition(), event, riskProfile))
                .sorted(Comparator.comparingInt(RiskRule::getPriority)) // Önceliğe göre sırala (düşük öncelik = yüksek önem)
                .collect(Collectors.toList());
    }

    // --- Kural değerlendirme motoru ---

    /**
     * Kural koşulunu değerlendirir
     * Basit bir DSL (Domain Specific Language) parser
     */
    private boolean evaluateCondition(String condition, Event event, RiskProfile riskProfile) {
        if (condition == null || condition.isBlank()) {
            return false;
        }

        String lowerCondition = condition.toLowerCase();

        // AND ile ayrılmış koşulları kontrol et
        if (lowerCondition.contains(" and ")) {
            String[] parts = lowerCondition.split(" and ");
            for (String part : parts) {
                if (!evaluateSingleCondition(part.trim(), event, riskProfile)) {
                    return false;
                }
            }
            return true;
        }

        // OR ile ayrılmış koşulları kontrol et
        if (lowerCondition.contains(" or ")) {
            String[] parts = lowerCondition.split(" or ");
            for (String part : parts) {
                if (evaluateSingleCondition(part.trim(), event, riskProfile)) {
                    return true;
                }
            }
            return false;
        }

        // Tek koşul
        return evaluateSingleCondition(lowerCondition, event, riskProfile);
    }

    /**
     * Tek bir koşulu değerlendirir
     */
    private boolean evaluateSingleCondition(String condition, Event event, RiskProfile riskProfile) {
        String meta = event.getMeta() != null ? event.getMeta().toLowerCase() : "";
        String service = event.getService() != null ? event.getService().toLowerCase() : "";
        String eventType = event.getEventType() != null ? event.getEventType().toLowerCase() : "";

        // Service kontrolü
        if (condition.contains("service=")) {
            String expectedService = extractValue(condition, "service=");
            if (!service.equals(expectedService.toLowerCase())) {
                return false;
            }
            return true;
        }

        // Event type kontrolü
        if (condition.contains("event_type=")) {
            String expectedType = extractValue(condition, "event_type=");
            if (!eventType.equals(expectedType.toLowerCase())) {
                return false;
            }
            return true;
        }

        // Amount kontrolü
        if (condition.contains("amount>")) {
            double threshold = Double.parseDouble(extractValue(condition, "amount>"));
            if (event.getAmount() == null || event.getAmount() <= threshold) {
                return false;
            }
            return true;
        }

        if (condition.contains("amount<")) {
            double threshold = Double.parseDouble(extractValue(condition, "amount<"));
            if (event.getAmount() == null || event.getAmount() >= threshold) {
                return false;
            }
            return true;
        }

        // Risk level kontrolü
        if (condition.contains("risk_level=")) {
            String expectedLevel = extractValue(condition, "risk_level=").toUpperCase();
            if (riskProfile.getRiskLevel() != RiskLevel.valueOf(expectedLevel)) {
                return false;
            }
            return true;
        }

        // Risk score kontrolü
        if (condition.contains("risk_score>")) {
            double threshold = Double.parseDouble(extractValue(condition, "risk_score>"));
            if (riskProfile.getRiskScore() == null || riskProfile.getRiskScore() <= threshold) {
                return false;
            }
            return true;
        }

        // Signal kontrolü
        if (condition.contains("signal=")) {
            String expectedSignal = extractValue(condition, "signal=");
            if (riskProfile.getSignals() == null || !riskProfile.getSignals().contains(expectedSignal)) {
                return false;
            }
            return true;
        }

        // Meta contains kontrolü
        if (condition.contains("meta contains ")) {
            String searchTerm = condition.substring(condition.indexOf("meta contains ") + 14).trim();
            if (!meta.contains(searchTerm.toLowerCase())) {
                return false;
            }
            return true;
        }

        // IP risk kontrolü
        if (condition.contains("ip_risk=high")) {
            if (!meta.contains("ip_risk=high")) {
                return false;
            }
            return true;
        }

        // New device kontrolü
        if (condition.contains("new_device") || condition.contains("yeni cihaz")) {
            if (!meta.contains("new_device")) {
                return false;
            }
            return true;
        }

        // Crypto kontrolü
        if (condition.contains("crypto")) {
            if (!meta.contains("crypto")) {
                return false;
            }
            return true;
        }

        // BiP servisi özel kontrolü (PDF örneği: "BiP yeni cihaz + ip_risk=high")
        if (condition.contains("bip") && condition.contains("yeni cihaz")) {
            return service.equals("bip") && meta.contains("new_device");
        }

        // Genel metin eşleşmesi (fallback)
        return meta.contains(condition) || service.contains(condition) || eventType.contains(condition);
    }

    /**
     * Koşuldan değer çıkarır (örn: "service=Paycell" -> "Paycell")
     */
    private String extractValue(String condition, String key) {
        int startIndex = condition.indexOf(key) + key.length();
        int endIndex = condition.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = condition.length();
        }
        return condition.substring(startIndex, endIndex).trim();
    }

    private RiskRuleResponse mapToResponse(RiskRule rule) {
        return new RiskRuleResponse(
                rule.getRuleId(),
                rule.getCondition(),
                rule.getAction(),
                rule.getPriority(),
                rule.getActive()
        );
    }
}