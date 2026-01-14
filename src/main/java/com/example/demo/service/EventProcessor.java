package com.example.demo.service;

import com.example.demo.api.EventIngestRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.DecisionRepository;
import com.example.demo.repository.EventLogRepository;
import com.example.demo.repository.RiskProfileRepository;
import com.example.demo.service.model.ActionResult;
import com.example.demo.service.model.RuleHit;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class EventProcessor {

    private final EventLogRepository eventLogRepository;
    private final RiskProfileRepository riskProfileRepository;
    private final DecisionRepository decisionRepository;

    private final RuleProcessor ruleProcessor;
    private final ActionProcessor actionProcessor;
    private final ActionService actionService;

    public EventProcessor(EventLogRepository eventLogRepository,
                          RiskProfileRepository riskProfileRepository,
                          DecisionRepository decisionRepository,
                          RuleProcessor ruleProcessor,
                          ActionProcessor actionProcessor,
                          ActionService actionService) {
        this.eventLogRepository = eventLogRepository;
        this.riskProfileRepository = riskProfileRepository;
        this.decisionRepository = decisionRepository;
        this.ruleProcessor = ruleProcessor;
        this.actionProcessor = actionProcessor;
        this.actionService = actionService;
    }

    @Transactional
    public Decision process(EventIngestRequest req) {

        // 1) Event oluştur + kaydet
        Event e = new Event();
        e.setEventId(req.eventId() != null ? req.eventId() : UUID.randomUUID().toString());
        e.setUserId(req.userId());
        e.setService(req.service());
        e.setEventType(req.eventType());
        e.setAmount(req.value()); // JSON value -> entity amount
        e.setUnit(req.unit());
        e.setMeta(req.meta());
        e.setTimestamp(
                req.timestamp() != null
                        ? LocalDateTime.ofInstant(req.timestamp(), ZoneOffset.UTC)
                        : LocalDateTime.now()
        );

        EventLog log = new EventLog();
        log.setId(UUID.randomUUID().toString());       // EventLog @Id alanın neyse onu set et
        log.setEventId(e.getEventId());
        log.setUserId(e.getUserId());
        log.setService(e.getService());
        log.setEventType(e.getEventType());
        log.setAmount(e.getAmount());
        log.setUnit(e.getUnit());
        log.setMeta(e.getMeta());
        log.setTimestamp(e.getTimestamp());

        eventLogRepository.save(log);

        // 2) RiskProfile bul/oluştur + güncelle
        RiskProfile p = riskProfileRepository.findByUserId(req.userId())
                .orElseGet(() -> {
                    RiskProfile rp = new RiskProfile();
                    rp.setUserId(req.userId());
                    rp.setRiskScore(0.0);
                    rp.setRiskLevel(RiskLevel.LOW);
                    return rp;
                });

        double delta = (e.getAmount() != null && e.getAmount() > 1000) ? 15 : 2;
        double newScore = Math.min(100.0, p.getRiskScore() + delta);
        p.setRiskScore(newScore);
        p.setRiskLevel(newScore >= 70 ? RiskLevel.HIGH : (newScore >= 30 ? RiskLevel.MEDIUM : RiskLevel.LOW));
        riskProfileRepository.save(p);

        // 3) Kuralları çalıştır
        List<RuleHit> hits = ruleProcessor.process(e, p);

        // 4) Aksiyon seç
        ActionResult actionResult = actionProcessor.process(hits);

        // 5) Decision yaz
        Decision d = new Decision();
        d.setDecisionId(UUID.randomUUID().toString());
        d.setUserId(req.userId());
        d.setTriggeredRules(hits.stream().map(RuleHit::ruleId).distinct().toList());
        d.setSelectedAction(actionResult.selectedAction());
        d.setSuppressedActions(actionResult.suppressedActions().stream().map(Enum::name).toList());
        d.setTimestamp(LocalDateTime.now());
        decisionRepository.save(d);

        // 6) Side-effect: action uygula
        if (actionResult.selectedAction() != null) {
            actionService.performAction(actionResult.selectedAction(), e);
        }

        return d;
    }
}