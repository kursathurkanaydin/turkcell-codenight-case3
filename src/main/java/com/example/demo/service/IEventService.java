package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.entity.Event;

import java.util.List;

/**
 * Event Service interface
 * Event yönetimi için temel operasyonlar
 */
public interface IEventService {

    /**
     * Yeni event alır ve işler
     * Bu metod event'i kaydeder ve risk motorunu tetikler
     * @param request Event request
     * @return İşlenen event
     */
    EventResponse processEvent(EventRequest request);

    /**
     * Event'i ID'ye göre getirir
     * @param eventId Event ID
     * @return Event entity
     */
    Event getEventById(String eventId);

    /**
     * Kullanıcının event'lerini getirir
     * @param userId Kullanıcı ID
     * @return Event listesi
     */
    List<EventResponse> getEventsByUserId(String userId);

    /**
     * Son event'leri getirir (dashboard için)
     * @param limit Limit
     * @return Event listesi
     */
    List<EventResponse> getRecentEvents(int limit);

    /**
     * Tüm event'leri getirir
     * @return Event listesi
     */
    List<EventResponse> getAllEvents();
}