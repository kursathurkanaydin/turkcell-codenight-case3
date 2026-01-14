package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Uygulama başladığında örnek verileri yükler
 * PDF'deki örnek verilere göre hazırlanmıştır
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RiskRuleRepository riskRuleRepository;
    private final RiskProfileRepository riskProfileRepository;

    public DataInitializer(UserRepository userRepository,
                           RiskRuleRepository riskRuleRepository,
                           RiskProfileRepository riskProfileRepository) {
        this.userRepository = userRepository;
        this.riskRuleRepository = riskRuleRepository;
        this.riskProfileRepository = riskProfileRepository;
    }

    @Override
    public void run(String... args) {
        // Kullanıcılar oluştur
        initializeUsers();

        // Risk kuralları oluştur
        initializeRiskRules();

        // Risk profilleri oluştur
        initializeRiskProfiles();

        System.out.println("=== Başlangıç verileri yüklendi ===");
    }

    private void initializeUsers() {
        if (userRepository.count() > 0) return;

        List<User> users = List.of(
                createUser("U1", "Ahmet Yılmaz", "İstanbul", "Premium"),
                createUser("U2", "Mehmet Demir", "Ankara", "Standard"),
                createUser("U3", "Ayşe Kaya", "İzmir", "Premium"),
                createUser("U4", "Fatma Şahin", "Bursa", "Standard"),
                createUser("U5", "Ali Öztürk", "Antalya", "VIP")
        );

        userRepository.saveAll(users);
        System.out.println("5 kullanıcı oluşturuldu.");
    }

    private User createUser(String id, String name, String city, String segment) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setCity(city);
        user.setSegment(segment);
        user.setBlocked(false);
        return user;
    }

    private void initializeRiskRules() {
        if (riskRuleRepository.count() > 0) return;

        List<RiskRule> rules = List.of(
                // PDF örneği: BiP yeni cihaz + ip_risk=high -> FORCE_2FA
                createRule("RR-01", "bip AND new_device AND ip_risk=high", ActionType.FORCE_2FA, 1),

                // Yüksek tutarlı kripto ödemesi -> OPEN_FRAUD_CASE
                createRule("RR-02", "service=Paycell AND meta contains crypto AND amount>500", ActionType.OPEN_FRAUD_CASE, 1),

                // Yüksek risk seviyesi -> TEMPORARY_BLOCK
                createRule("RR-03", "risk_level=HIGH", ActionType.TEMPORARY_BLOCK, 2),

                // Orta seviye risk + yüksek tutar -> PAYMENT_REVIEW
                createRule("RR-04", "risk_level=MEDIUM AND amount>1000", ActionType.PAYMENT_REVIEW, 3),

                // Yeni cihazdan yüksek tutarlı işlem -> FORCE_2FA
                createRule("RR-05", "new_device AND amount>2000", ActionType.FORCE_2FA, 2),

                // VPN kullanımı -> ANOMALY_ALERT
                createRule("RR-06", "meta contains vpn", ActionType.ANOMALY_ALERT, 4),

                // Yabancı IP'den işlem -> PAYMENT_REVIEW
                createRule("RR-07", "meta contains foreign", ActionType.PAYMENT_REVIEW, 3),

                // Şifre değişikliği sonrası yüksek tutar -> FORCE_2FA
                createRule("RR-08", "signal=password_changed AND amount>500", ActionType.FORCE_2FA, 1),

                // Çok yüksek tutarlı işlem -> OPEN_FRAUD_CASE
                createRule("RR-09", "amount>10000", ActionType.OPEN_FRAUD_CASE, 1),

                // Cihaz değişikliği + yüksek IP riski -> TEMPORARY_BLOCK
                createRule("RR-10", "signal=device_changed AND ip_risk=high", ActionType.TEMPORARY_BLOCK, 1)
        );

        riskRuleRepository.saveAll(rules);
        System.out.println("10 risk kuralı oluşturuldu.");
    }

    private RiskRule createRule(String ruleId, String condition, ActionType action, int priority) {
        RiskRule rule = new RiskRule();
        rule.setRuleId(ruleId);
        rule.setCondition(condition);
        rule.setAction(action);
        rule.setPriority(priority);
        rule.setActive(true);
        return rule;
    }

    private void initializeRiskProfiles() {
        if (riskProfileRepository.count() > 0) return;

        List<RiskProfile> profiles = List.of(
                createProfile("U1", 15.0, RiskLevel.LOW, new ArrayList<>()),
                createProfile("U2", 45.0, RiskLevel.MEDIUM, List.of("multiple_devices")),
                createProfile("U3", 25.0, RiskLevel.LOW, new ArrayList<>()),
                createProfile("U4", 60.0, RiskLevel.MEDIUM, List.of("foreign_transaction")),
                createProfile("U5", 85.0, RiskLevel.HIGH, List.of("high_ip_risk_new_device", "crypto_payment"))
        );

        riskProfileRepository.saveAll(profiles);
        System.out.println("5 risk profili oluşturuldu.");
    }

    private RiskProfile createProfile(String userId, double score, RiskLevel level, List<String> signals) {
        RiskProfile profile = new RiskProfile();
        profile.setUserId(userId);
        profile.setRiskScore(score);
        profile.setRiskLevel(level);
        profile.setSignals(new ArrayList<>(signals));
        return profile;
    }
}