package com.example.demo.service;

import com.example.demo.dto.response.RiskProfileResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.RiskLevel;
import com.example.demo.entity.RiskProfile;
import com.example.demo.repository.RiskProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Risk Profile Service implementasyonu
 * Kullanıcı risk profillerini yönetir ve event'lere göre günceller
 */
@Service
public class RiskProfileService implements IRiskProfileService {

    private final RiskProfileRepository riskProfileRepository;

    public RiskProfileService(RiskProfileRepository riskProfileRepository) {
        this.riskProfileRepository = riskProfileRepository;
    }

    @Override
    public RiskProfileResponse getRiskProfile(String userId) {
        RiskProfile profile = getOrCreateRiskProfile(userId);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public RiskProfile updateRiskProfile(Event event) {
        RiskProfile profile = getOrCreateRiskProfile(event.getUserId());

        // Event'e göre risk skoru ve sinyalleri güncelle
        double scoreDelta = calculateScoreDelta(event);
        List<String> newSignals = detectSignals(event);

        // Skoru güncelle (0-100 arasında tut)
        double newScore = Math.max(0, Math.min(100, profile.getRiskScore() + scoreDelta));
        profile.setRiskScore(newScore);

        // Sinyalleri ekle
        List<String> currentSignals = profile.getSignals() != null
                ? new ArrayList<>(profile.getSignals())
                : new ArrayList<>();
        for (String signal : newSignals) {
            if (!currentSignals.contains(signal)) {
                currentSignals.add(signal);
            }
        }
        profile.setSignals(currentSignals);

        // Risk seviyesini güncelle
        profile.setRiskLevel(calculateRiskLevel(newScore));

        return riskProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void addSignal(String userId, String signal) {
        RiskProfile profile = getOrCreateRiskProfile(userId);
        List<String> signals = profile.getSignals() != null
                ? new ArrayList<>(profile.getSignals())
                : new ArrayList<>();

        if (!signals.contains(signal)) {
            signals.add(signal);
            profile.setSignals(signals);
            riskProfileRepository.save(profile);
        }
    }

    @Override
    @Transactional
    public void updateRiskScore(String userId, double scoreDelta) {
        RiskProfile profile = getOrCreateRiskProfile(userId);
        double newScore = Math.max(0, Math.min(100, profile.getRiskScore() + scoreDelta));
        profile.setRiskScore(newScore);
        profile.setRiskLevel(calculateRiskLevel(newScore));
        riskProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void resetRiskProfile(String userId) {
        RiskProfile profile = getOrCreateRiskProfile(userId);
        profile.setRiskScore(0.0);
        profile.setRiskLevel(RiskLevel.LOW);
        profile.setSignals(new ArrayList<>());
        riskProfileRepository.save(profile);
    }

    @Override
    public List<RiskProfileResponse> getAllRiskProfiles() {
        return riskProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskProfileResponse> getHighRiskProfiles() {
        return riskProfileRepository.findAll()
                .stream()
                .filter(p -> p.getRiskLevel() == RiskLevel.HIGH)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Helper metodlar ---

    /**
     * Kullanıcının risk profilini getirir veya yoksa oluşturur
     */
    private RiskProfile getOrCreateRiskProfile(String userId) {
        return riskProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    RiskProfile newProfile = new RiskProfile();
                    newProfile.setUserId(userId);
                    newProfile.setRiskScore(0.0);
                    newProfile.setRiskLevel(RiskLevel.LOW);
                    newProfile.setSignals(new ArrayList<>());
                    return riskProfileRepository.save(newProfile);
                });
    }

    /**
     * Event'e göre risk skoru değişimini hesaplar
     */
    private double calculateScoreDelta(Event event) {
        double delta = 0;

        // Servis bazlı risk artışı
        String service = event.getService() != null ? event.getService().toUpperCase() : "";
        switch (service) {
            case "PAYCELL" -> delta += 10; // Ödeme işlemleri risk artırır
            case "BIP" -> delta += 5;
            case "SUPERONLINE" -> delta += 3;
            case "TV+" -> delta += 2;
        }

        // Event tipi bazlı risk artışı
        String eventType = event.getEventType() != null ? event.getEventType().toUpperCase() : "";
        switch (eventType) {
            case "PAYMENT" -> delta += 15;
            case "LOGIN" -> delta += 5;
            case "PASSWORD_CHANGE" -> delta += 20;
            case "DEVICE_CHANGE" -> delta += 25;
            case "TRANSFER" -> delta += 20;
        }

        // Yüksek tutarlı işlemler
        if (event.getAmount() != null) {
            if (event.getAmount() > 5000) delta += 30;
            else if (event.getAmount() > 1000) delta += 15;
            else if (event.getAmount() > 500) delta += 5;
        }

        // Meta bilgisi analizi
        String meta = event.getMeta() != null ? event.getMeta().toLowerCase() : "";
        if (meta.contains("crypto")) delta += 25;
        if (meta.contains("ip_risk=high")) delta += 20;
        if (meta.contains("new_device")) delta += 15;
        if (meta.contains("vpn")) delta += 10;
        if (meta.contains("foreign")) delta += 10;

        return delta;
    }

    /**
     * Event'ten risk sinyallerini tespit eder
     */
    private List<String> detectSignals(Event event) {
        List<String> signals = new ArrayList<>();

        String meta = event.getMeta() != null ? event.getMeta().toLowerCase() : "";
        String eventType = event.getEventType() != null ? event.getEventType().toLowerCase() : "";
        String service = event.getService() != null ? event.getService().toLowerCase() : "";

        // Meta bazlı sinyaller
        if (meta.contains("ip_risk=high") && meta.contains("new_device")) {
            signals.add("high_ip_risk_new_device");
        }
        if (meta.contains("crypto")) {
            signals.add("crypto_payment");
        }
        if (meta.contains("vpn")) {
            signals.add("vpn_detected");
        }
        if (meta.contains("foreign") || meta.contains("abroad")) {
            signals.add("foreign_transaction");
        }

        // Yüksek tutarlı işlem sinyali
        if (event.getAmount() != null && event.getAmount() > 5000) {
            signals.add("high_value_transaction");
        }

        // Event tipi bazlı sinyaller
        if (eventType.equals("device_change")) {
            signals.add("device_changed");
        }
        if (eventType.equals("password_change")) {
            signals.add("password_changed");
        }

        // Servis bazlı sinyaller
        if (service.equals("paycell") && event.getAmount() != null && event.getAmount() > 1000) {
            signals.add("large_paycell_payment");
        }

        return signals;
    }

    /**
     * Skor'a göre risk seviyesini hesaplar
     */
    private RiskLevel calculateRiskLevel(double score) {
        if (score >= 70) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private RiskProfileResponse mapToResponse(RiskProfile profile) {
        return new RiskProfileResponse(
                profile.getUserId(),
                profile.getRiskScore(),
                profile.getRiskLevel(),
                profile.getSignals()
        );
    }
}