package com.example.demo.service;

import com.example.demo.entity.Event;
import com.example.demo.entity.RiskProfile;
import com.example.demo.entity.RiskRule;
import com.example.demo.repository.RiskRuleRepository;
import com.example.demo.service.model.RuleHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleProcessor {

    private final RiskRuleRepository riskRuleRepository;

    public RuleProcessor(RiskRuleRepository riskRuleRepository) {
        this.riskRuleRepository = riskRuleRepository;
    }

    public List<RuleHit> process(Event event, RiskProfile profile) {
        return riskRuleRepository.findByIsActiveTrue().stream()
                .filter(r -> matches(r.getCondition(), event, profile))
                .map(r -> new RuleHit(
                        r.getRuleId(),
                        r.getPriority() != null ? r.getPriority() : 999,
                        r.getAction()
                ))
                .toList();
    }

    // minimal DSL: "service=Paycell; eventType=PAYMENT; amount>900"
    private boolean matches(String condition, Event e, RiskProfile p) {
        if (condition == null || condition.isBlank()) return true;

        String[] parts = condition.split(";");
        for (String raw : parts) {
            String c = raw.trim();
            if (c.isEmpty()) continue;

            if (c.startsWith("service=")) {
                String v = c.substring("service=".length()).trim();
                if (e.getService() == null || !v.equalsIgnoreCase(e.getService())) return false;

            } else if (c.startsWith("eventType=")) {
                String v = c.substring("eventType=".length()).trim();
                if (e.getEventType() == null || !v.equalsIgnoreCase(e.getEventType())) return false;

            } else if (c.startsWith("amount>")) {
                double v = Double.parseDouble(c.substring("amount>".length()).trim());
                if (e.getAmount() == null || e.getAmount() <= v) return false;

            } else if (c.startsWith("riskScore>")) {
                double v = Double.parseDouble(c.substring("riskScore>".length()).trim());
                if (p.getRiskScore() == null || p.getRiskScore() <= v) return false;

            } else {
                return false;
            }
        }
        return true;
    }
}