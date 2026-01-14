package com.example.demo.service;

import com.example.demo.dto.response.DashboardSummaryResponse;
import com.example.demo.dto.response.DecisionResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.DecisionRepository;
import com.example.demo.repository.FraudCaseRepository;
import com.example.demo.repository.RiskProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Risk Engine Service implementasyonu
 * SİSTEMİN KALBİ - Tüm risk değerlendirme akışını yönetir
 *
 * Ana Akış (PDF'e göre):
 * 1. Event alınır
 * 2. Risk profili güncellenir (skor ve sinyaller)
 * 3. Kurallar değerlendirilir (hangi kurallar tetiklendi)
 * 4. Birden fazla aksiyon varsa önceliğe göre seçim yapılır
 * 5. Karar kaydedilir (audit log - tetiklenen kurallar, seçilen aksiyon, bastırılan aksiyonlar)
 * 6. Seçilen aksiyon yürütülür
 */
@Service
public class RiskEngineService implements IRiskEngineService {

    private final IRiskProfileService riskProfileService;
    private final IRiskRuleService riskRuleService;
    private final IDecisionService decisionService;
    private final ActionService actionService;
    private final IEventService eventService;

    // Dashboard için repository'ler
    private final RiskProfileRepository riskProfileRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final DecisionRepository decisionRepository;

    public RiskEngineService(IRiskProfileService riskProfileService,
                             IRiskRuleService riskRuleService,
                             IDecisionService decisionService,
                             ActionService actionService,
                             @Lazy IEventService eventService,  // Lazy to avoid circular dependency
                             RiskProfileRepository riskProfileRepository,
                             FraudCaseRepository fraudCaseRepository,
                             DecisionRepository decisionRepository) {
        this.riskProfileService = riskProfileService;
        this.riskRuleService = riskRuleService;
        this.decisionService = decisionService;
        this.actionService = actionService;
        this.eventService = eventService;
        this.riskProfileRepository = riskProfileRepository;
        this.fraudCaseRepository = fraudCaseRepository;
        this.decisionRepository = decisionRepository;
    }

    @Override
    @Transactional
    public void processEvent(Event event) {
        System.out.println("=== Risk Engine Processing Event: " + event.getEventId() + " ===");

        // ADIM 1: Risk profilini güncelle
        RiskProfile riskProfile = riskProfileService.updateRiskProfile(event);
        System.out.println("Risk profili güncellendi: User=" + event.getUserId() +
                ", Score=" + riskProfile.getRiskScore() +
                ", Level=" + riskProfile.getRiskLevel() +
                ", Signals=" + riskProfile.getSignals());

        // ADIM 2: Kuralları değerlendir
        List<RiskRule> triggeredRules = riskRuleService.evaluateRules(event, riskProfile);

        if (triggeredRules.isEmpty()) {
            System.out.println("Tetiklenen kural yok. İşlem normal devam ediyor.");
            return;
        }

        System.out.println("Tetiklenen kurallar: " +
                triggeredRules.stream().map(RiskRule::getRuleId).collect(Collectors.toList()));

        // ADIM 3: Aksiyonları belirle ve önceliğe göre seç
        // En düşük priority değeri = en yüksek öncelik
        RiskRule selectedRule = triggeredRules.get(0); // Zaten önceliğe göre sıralı
        ActionType selectedAction = selectedRule.getAction();

        // Bastırılan (seçilmeyen) aksiyonları belirle
        List<ActionType> suppressedActions = triggeredRules.stream()
                .skip(1)  // İlk kural zaten seçildi
                .map(RiskRule::getAction)
                .distinct()
                .filter(action -> action != selectedAction)  // Aynı aksiyonu bastırılmış olarak gösterme
                .collect(Collectors.toList());

        System.out.println("Seçilen aksiyon: " + selectedAction +
                " (Kural: " + selectedRule.getRuleId() + ", Priority: " + selectedRule.getPriority() + ")");
        if (!suppressedActions.isEmpty()) {
            System.out.println("Bastırılan aksiyonlar: " + suppressedActions);
        }

        // ADIM 4: Karar kaydet (Audit Log)
        decisionService.recordDecision(event.getUserId(), triggeredRules, selectedAction, suppressedActions);

        // ADIM 5: Seçilen aksiyonu yürüt
        try {
            actionService.performAction(selectedAction, event);
            System.out.println("Aksiyon başarıyla yürütüldü: " + selectedAction);
        } catch (Exception e) {
            System.err.println("Aksiyon yürütülürken hata: " + e.getMessage());
        }

        System.out.println("=== Event işleme tamamlandı ===\n");
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        // Son event'ler
        List<EventResponse> recentEvents = eventService.getRecentEvents(10);

        // Risk profil istatistikleri
        List<RiskProfile> allProfiles = riskProfileRepository.findAll();
        long totalUsers = allProfiles.size();
        long highRiskUsers = allProfiles.stream()
                .filter(p -> p.getRiskLevel() == RiskLevel.HIGH).count();
        long mediumRiskUsers = allProfiles.stream()
                .filter(p -> p.getRiskLevel() == RiskLevel.MEDIUM).count();
        long lowRiskUsers = allProfiles.stream()
                .filter(p -> p.getRiskLevel() == RiskLevel.LOW).count();

        // Fraud case istatistikleri
        List<FraudCase> allCases = fraudCaseRepository.findAll();
        long openCases = allCases.stream()
                .filter(c -> c.getStatus() == CaseStatus.OPEN).count();
        long inProgressCases = allCases.stream()
                .filter(c -> c.getStatus() == CaseStatus.IN_PROGRESS).count();
        long closedCases = allCases.stream()
                .filter(c -> c.getStatus() == CaseStatus.CLOSED).count();

        // Son kararlar
        List<DecisionResponse> recentDecisions = decisionService.getRecentDecisions(10);

        // Kural tetiklenme istatistikleri
        Map<String, Long> ruleTriggeredCounts = decisionRepository.findAll().stream()
                .flatMap(d -> d.getTriggeredRules().stream())
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

        // Aksiyon istatistikleri
        Map<String, Long> actionExecutedCounts = decisionRepository.findAll().stream()
                .filter(d -> d.getSelectedAction() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getSelectedAction().name(),
                        Collectors.counting()));

        return new DashboardSummaryResponse(
                recentEvents,
                totalUsers,
                highRiskUsers,
                mediumRiskUsers,
                lowRiskUsers,
                openCases,
                inProgressCases,
                closedCases,
                recentDecisions,
                ruleTriggeredCounts,
                actionExecutedCounts
        );
    }
}