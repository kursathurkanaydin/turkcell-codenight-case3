package com.example.demo.service;

import com.example.demo.dto.request.CreateFraudCaseRequest;
import com.example.demo.entity.CaseAction;
import com.example.demo.entity.CaseStatus;
import com.example.demo.entity.FraudCase;
import com.example.demo.repository.ActionServiceRepository;
import com.example.demo.repository.FraudCaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudCaseService implements IFraudCaseService {

    private final FraudCaseRepository caseRepository;
    private final ActionServiceRepository actionRepository;
    public FraudCaseService(FraudCaseRepository caseRepository, ActionServiceRepository actionRepository) {
        this.caseRepository = caseRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional // Hem Case'i hem Action'ı aynı anda kaydetmek için
    public FraudCase createSystemCase(CreateFraudCaseRequest request) {

        // 1. Case Oluştur [cite: 63-68]
        FraudCase fraudCase = new FraudCase();
        fraudCase.setUserId(request.userId());
        fraudCase.setStatus(CaseStatus.OPEN);
        fraudCase.setPriority(request.priority());
        fraudCase.setOpenedAt(LocalDateTime.now());

        FraudCase savedCase = caseRepository.save(fraudCase);

        logAction(savedCase, "SYSTEM", "CASE_CREATED", "Otomatik oluşturuldu: " + request.ruleDescription());
        return savedCase;
    }


    @Transactional
    public void updateCaseStatus(String caseId, CaseStatus newStatus, String actorName, String note) {
        // Case'i bul
        FraudCase fraudCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case bulunamadı"));

        String oldStatus = fraudCase.getStatus().name();

        // Statüyü güncelle
        fraudCase.setStatus(newStatus); // Örn: OPEN -> CLOSED
        caseRepository.save(fraudCase);

        // Log at
        logAction(fraudCase, actorName, "STATUS_CHANGE",
                String.format("Statü değişti: %s -> %s. Not: %s", oldStatus, newStatus, note));
    }

    /**
     * SENARYO 3: DASHBOARD İÇİN LİSTELEME
     * "Açık Fraud Case'ler ve durumları" [cite: 97]
     */
    public List<FraudCase> getOpenCases() {
        return caseRepository.findByStatus(CaseStatus.OPEN);
    }

    public List<FraudCase> getAllCases() {
        return caseRepository.findAll();
    }

    // Detay görüntüleme (Bonus: Geçmişi ile birlikte)
    public FraudCase getCaseDetails(String caseId) {
        return caseRepository.findById(caseId).orElseThrow();
    }

    // --- YARDIMCI METOD (PRIVATE) ---
    // Kod tekrarını önlemek için loglama işini buraya aldık.
    private void logAction(FraudCase fraudCase, String actor, String actionType, String note) {
        CaseAction action = new CaseAction();
        action.setFraudCase(fraudCase);
        action.setActor(actor);         // "System" veya "Ahmet_Analist"
        action.setActionType(actionType); // "CASE_CREATED", "STATUS_CHANGE"
        action.setNote(note);
        action.setTimestamp(LocalDateTime.now());

        actionRepository.save(action);
    }
}