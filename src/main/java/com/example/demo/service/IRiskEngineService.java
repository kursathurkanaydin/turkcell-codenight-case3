package com.example.demo.service;

import com.example.demo.dto.response.DashboardSummaryResponse;
import com.example.demo.entity.Event;

/**
 * Risk Engine Service interface
 * Ana orkestrasyon servisi - tüm risk değerlendirme akışını yönetir
 *
 * Akış:
 * 1. Event alınır
 * 2. Risk profili güncellenir
 * 3. Kurallar değerlendirilir
 * 4. Tetiklenen kurallardan en yüksek öncelikli aksiyon seçilir
 * 5. Karar kaydedilir (tetiklenen kurallar, seçilen aksiyon, bastırılan aksiyonlar)
 * 6. Seçilen aksiyon yürütülür
 */
public interface IRiskEngineService {

    /**
     * Event'i işler ve tüm risk değerlendirme akışını çalıştırır
     * @param event İşlenecek event
     */
    void processEvent(Event event);

    /**
     * Dashboard özeti getirir
     * @return Dashboard özeti
     */
    DashboardSummaryResponse getDashboardSummary();
}