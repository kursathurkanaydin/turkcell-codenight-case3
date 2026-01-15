package com.example.demo.service;

import com.example.demo.dto.request.RiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.RiskLevel;
import com.example.demo.entity.RiskProfile;
import com.example.demo.entity.RiskRule;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.RiskRuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final EventRepository eventRepository;

    public RiskRuleService(RiskRuleRepository riskRuleRepository, EventRepository eventRepository) {
        this.riskRuleRepository = riskRuleRepository;
        this.eventRepository = eventRepository;
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
        rule.setActive(request.active() != null ? request.active() : true);

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
        if (request.active() != null) {
            rule.setActive(request.active());
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
        List<RiskRule> triggeredRules = new java.util.ArrayList<>();

        System.out.println("Değerlendirilecek aktif kural sayısı: " + activeRules.size());

        // Kullanıcı isteği üzerine explicit for loop ile detaylı kontrol
        for (RiskRule rule : activeRules) {
            boolean isMatched = evaluateCondition(rule.getCondition(), event, riskProfile);
            
            if (isMatched) {
                System.out.println("[MATCH] Kural Eşleşti: " + rule.getRuleId() + " Priority: " + rule.getPriority());
                triggeredRules.add(rule);
            }
        }

        // Önceliğe göre sırala (düşük öncelik = yüksek önem)
        triggeredRules.sort(Comparator.comparingInt(RiskRule::getPriority));
        return triggeredRules;
    }

    // --- Kural değerlendirme motoru ---

    /**
     * Kural koşulunu değerlendirir
     */
    private boolean evaluateCondition(String condition, Event event, RiskProfile riskProfile) {
        if (condition == null || condition.isBlank()) {
            return false;
        }

        // Split by AND (&&) but ignore && inside 'single quotes'
        // Regex lookbehind/ahead is complex, so we will use a simpler split-based approach since we know the format
        // BUT for this specific assignment, a simple split by " && " usually works if spaces are consistent.
        // Let's assume the spacing " && " is consistent or at least present. 
        // If not, we might need a custom parser.
        
        // A simple regex that splits by && unless preceded by a quote might fail for complex cases.
        // Let's stick to the previous simple split but robustify the individual checks.
        
        String[] parts = condition.split("&&| AND ");

        for (String part : parts) {
            if (!evaluateSingleCondition(part.trim(), event, riskProfile)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tek bir koşulu değerlendirir
     */
    private boolean evaluateSingleCondition(String condition, Event event, RiskProfile riskProfile) {
        String trimmed = condition.trim();
        
        // Check "contains" first
        if (trimmed.toLowerCase().contains(" contains ")) {
            return evaluateContains(trimmed, event, riskProfile);
        } 
        
        // Check equality "=="
        if (trimmed.contains("==")) {
            return evaluateEquality(trimmed, event, riskProfile);
        }
        
        // Check comparison operators
        if (trimmed.contains(">=")) return evaluateComparison(trimmed, event, riskProfile, ">=");
        if (trimmed.contains("<=")) return evaluateComparison(trimmed, event, riskProfile, "<=");
        if (trimmed.contains(">")) return evaluateComparison(trimmed, event, riskProfile, ">");
        if (trimmed.contains("<")) return evaluateComparison(trimmed, event, riskProfile, "<");

        // Fallback or boolean flag check (e.g. meta contains 'x')
        // Sometimes "meta contains 'x'" might be parsed here if "contains" check above missed it? 
        // No, the first check should catch it.
        
        return false;
    }

    private boolean evaluateContains(String condition, Event event, RiskProfile riskProfile) {
        // format: field contains 'value'
        // Example: meta contains 'device=new'
        String[] parts = condition.split(" (?i)contains "); // case-insensitive split
        if (parts.length != 2) return false;
        
        String field = parts[0].trim();
        String value = stripQuotes(parts[1].trim());
        
        String actualValue = getFieldValueAsString(field, event, riskProfile);
        
        // Special case for 'meta': check if the actual meta string contains the substring
        // OR if meta is treated as a map. The CSV says: meta contains 'device=new'
        // Event meta is "device=new, ip_risk=high"
        if (actualValue == null) return false;

        return actualValue.toLowerCase().contains(value.toLowerCase());
    }

    private boolean evaluateEquality(String condition, Event event, RiskProfile riskProfile) {
        // format: field == 'value'
        String[] parts = condition.split("==");
        if (parts.length != 2) return false;
        
        String field = parts[0].trim();
        String value = stripQuotes(parts[1].trim());
        
        String actualValue = getFieldValueAsString(field, event, riskProfile);
        return actualValue != null && actualValue.equalsIgnoreCase(value);
    }
    
    private boolean evaluateComparison(String condition, Event event, RiskProfile riskProfile, String operator) {
        String[] parts = condition.split(operator);
        if (parts.length != 2) return false;
        
        String field = parts[0].trim();
        String targetValStr = stripQuotes(parts[1].trim());
        
        Double actualValue = getFieldValueAsDouble(field, event, riskProfile);
        if (actualValue == null) return false;
        
        try {
            Double targetValue = Double.parseDouble(targetValStr);
            switch (operator) {
                case ">": return actualValue > targetValue;
                case "<": return actualValue < targetValue;
                case ">=": return actualValue >= targetValue;
                case "<=": return actualValue <= targetValue;
                default: return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getFieldValueAsString(String field, Event event, RiskProfile riskProfile) {
        switch (field.toLowerCase()) {
            case "service": return event.getService();
            case "event_type": return event.getEventType();
            case "unit": return event.getUnit();
            case "meta": return event.getMeta();
            case "risk_level": return riskProfile.getRiskLevel() != null ? riskProfile.getRiskLevel().name() : null;
            case "signals": 
                return riskProfile.getSignals() != null ? String.join("|", riskProfile.getSignals()) : "";
            default: return "";
        }
    }
    
    private Double getFieldValueAsDouble(String field, Event event, RiskProfile riskProfile) {
        switch (field.toLowerCase()) {
            case "value":
            case "amount":
                return event.getAmount();
            case "risk_score":
                return riskProfile.getRiskScore();
            case "payments_15min_count":
                // RR-03 kuralı için: Son 15 dakikadaki ödeme sayısı
                LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
                long count = eventRepository.countPaymentEventsInTimeWindow(
                        event.getUserId(),
                        event.getService(),
                        fifteenMinutesAgo
                );
                System.out.println("payments_15min_count for user " + event.getUserId() + ": " + count);
                return (double) count;
            default:
                // Try to find in meta (e.g., "payments_15min_count=2")
                return extractDoubleFromMeta(event.getMeta(), field);
        }
    }

    private Double extractDoubleFromMeta(String meta, String field) {
        if (meta == null || meta.isBlank()) return 0.0;
        
        // Simple search for "field=value" or "field:value"
        // Splitting by comma to handle multiple meta entries
        String[] parts = meta.split("[,|]");
        for (String part : parts) {
            String[] kv = part.split("[=:]");
            if (kv.length == 2 && kv[0].trim().equalsIgnoreCase(field)) {
                try {
                    return Double.parseDouble(kv[1].trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0; // Default to 0 if not found
    }

    private String stripQuotes(String val) {
        if (val == null) return "";
        if ((val.startsWith("'") && val.endsWith("'")) || (val.startsWith("\"") && val.endsWith("\""))) {
            return val.substring(1, val.length() - 1);
        }
        return val;
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