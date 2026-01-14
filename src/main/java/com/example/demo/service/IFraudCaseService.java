package com.example.demo.service;

import com.example.demo.dto.request.CreateFraudCaseRequest;
import com.example.demo.entity.CaseStatus;
import com.example.demo.entity.FraudCase;

import java.util.List;

/**
 * Fraud Case Service interface
 * SOLID: Dependency Inversion - Concrete implementasyon yerine interface'e bağımlılık
 * SOLID: Interface Segregation - Sadece gerekli metodları içerir
 */
public interface IFraudCaseService {

    /**
     * Sistem tarafından otomatik fraud case oluşturur
     * @param request Case oluşturma request'i
     * @return Oluşturulan FraudCase
     */
    FraudCase createSystemCase(CreateFraudCaseRequest request);

    /**
     * Fraud case'in statüsünü günceller
     * @param caseId Case ID
     * @param newStatus Yeni status
     * @param actorName İşlemi yapan kişi
     * @param note Not
     */
    void updateCaseStatus(String caseId, CaseStatus newStatus, String actorName, String note);

    /**
     * Açık fraud case'leri listeler
     * @return Açık case'ler
     */
    List<FraudCase> getOpenCases();

    /**
     * Tüm fraud case'leri listeler
     * @return Tüm case'ler
     */
    List<FraudCase> getAllCases();

    /**
     * Case detaylarını getirir
     * @param caseId Case ID
     * @return FraudCase detayı
     */
    FraudCase getCaseDetails(String caseId);
}