package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Uygulama başladığında CSV dosyalarından verileri yükler.
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
        // Kullanıcılar oluştur (users.csv)
        initializeUsers();

        // Risk kuralları oluştur (risk_rules.csv)
        initializeRiskRules();

        // Risk profilleri oluştur (risk_profiles.csv)
        initializeRiskProfiles();

        System.out.println("=== Başlangıç verileri CSV dosyalarından yüklendi ===");
        
        // Garanti olsun: Profili olmayan kullanıcı kalmasın
        ensureAllUsersHaveProfiles();
    }

    private void ensureAllUsersHaveProfiles() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
             if (!riskProfileRepository.existsById(user.getId())) {
                 RiskProfile profile = new RiskProfile();
                 profile.setUserId(user.getId());
                 profile.setRiskScore(0.0);
                 profile.setRiskLevel(RiskLevel.LOW);
                 profile.setSignals(new ArrayList<>());
                 riskProfileRepository.save(profile);
                 System.out.println("Otomatik profil oluşturuldu: " + user.getId());
             }
        }
    }

    private void initializeUsers() {
        if (userRepository.count() > 0) return;

        String file = "users.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            List<User> users = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Header
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1); // CSV parsing simplified
                if (parts.length >= 4) {
                    User user = new User();
                    user.setId(parts[0].trim());
                    user.setName(parts[1].trim());
                    user.setCity(parts[2].trim());
                    user.setSegment(parts[3].trim());
                    user.setBlocked(false);
                    users.add(user);
                }
            }
            userRepository.saveAll(users);
            System.out.println(users.size() + " kullanıcı yüklendi.");
        } catch (IOException e) {
            System.err.println("users.csv okunamadı: " + e.getMessage());
        }
    }

    private void initializeRiskRules() {
        if (riskRuleRepository.count() > 0) return;

        String file = "risk_rules.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            List<RiskRule> rules = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Header
                if (line.trim().isEmpty()) continue;

                // Simple split won't work perfectly if condition contains commas,
                // but assuming the provided CSV format is consistent.
                // Or better, we can assume the format based on the known columns.
                // rule_id,condition,action,priority,is_active
                
                // Let's do a slightly smarter split.
                // Finding the first comma for ID
                int firstComma = line.indexOf(',');
                if (firstComma == -1) continue;
                String ruleId = line.substring(0, firstComma).trim();
                
                // Finding the last 3 commas for active, priority, action
                String remainder = line.substring(firstComma + 1);
                int lastComma = remainder.lastIndexOf(',');
                String isActiveStr = remainder.substring(lastComma + 1).trim();
                remainder = remainder.substring(0, lastComma);
                
                lastComma = remainder.lastIndexOf(',');
                String priorityStr = remainder.substring(lastComma + 1).trim();
                remainder = remainder.substring(0, lastComma);
                
                lastComma = remainder.lastIndexOf(',');
                String actionStr = remainder.substring(lastComma + 1).trim();
                
                // The rest is the condition
                String condition = remainder.substring(0, lastComma).trim();

                RiskRule rule = new RiskRule();
                rule.setRuleId(ruleId);
                rule.setCondition(condition);
                
                try {
                    rule.setAction(ActionType.valueOf(actionStr));
                } catch (IllegalArgumentException e) {
                    System.err.println("Geçersiz ActionType: " + actionStr + " Kural: " + ruleId);
                    continue; // Skip invalid action
                }
                
                try {
                    rule.setPriority(Integer.parseInt(priorityStr));
                } catch (NumberFormatException e) {
                   rule.setPriority(1); // Default
                }
                
                // Robust boolean parsing
                boolean isActive = Boolean.parseBoolean(isActiveStr) || "TRUE".equalsIgnoreCase(isActiveStr);
                rule.setActive(isActive);
                
                rules.add(rule);
            }
            riskRuleRepository.saveAll(rules);
            System.out.println(rules.size() + " risk kuralı yüklendi.");
        } catch (IOException e) {
            System.err.println("risk_rules.csv okunamadı: " + e.getMessage());
        }
    }

    private void initializeRiskProfiles() {
        if (riskProfileRepository.count() > 0) return;

        String file = "risk_profiles.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            List<RiskProfile> profiles = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Header
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    RiskProfile profile = new RiskProfile();
                    profile.setUserId(parts[0].trim());
                    profile.setRiskScore(Double.parseDouble(parts[1].trim()));
                    try {
                        profile.setRiskLevel(RiskLevel.valueOf(parts[2].trim()));
                    } catch (IllegalArgumentException e) {
                         profile.setRiskLevel(RiskLevel.LOW);
                    }
                    
                    String signalsStr = parts[3].trim();
                    List<String> signals = new ArrayList<>();
                    if (!signalsStr.isEmpty()) {
                        String[] signalParts = signalsStr.split("\\|");
                        signals.addAll(Arrays.asList(signalParts));
                    }
                    profile.setSignals(signals);
                    
                    profiles.add(profile);
                }
            }
            riskProfileRepository.saveAll(profiles);
            System.out.println(profiles.size() + " risk profili yüklendi.");
        } catch (IOException e) {
            System.err.println("risk_profiles.csv okunamadı: " + e.getMessage());
        }
    }
}