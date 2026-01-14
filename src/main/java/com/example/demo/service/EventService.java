package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.entity.Event;
import com.example.demo.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Event Service implementasyonu
 * Event'leri alır, kaydeder ve risk motorunu tetikler
 */
@Service
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final IRiskEngineService riskEngineService;

    public EventService(EventRepository eventRepository, IRiskEngineService riskEngineService) {
        this.eventRepository = eventRepository;
        this.riskEngineService = riskEngineService;
    }

    @Override
    @Transactional
    public EventResponse processEvent(EventRequest request) {
        // 1. Event entity oluştur
        Event event = new Event();
        event.setEventId(request.eventId() != null && !request.eventId().isBlank()
                ? request.eventId()
                : "EV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        event.setUserId(request.userId());
        event.setService(request.service());
        event.setEventType(request.eventType());
        event.setAmount(request.value());
        event.setUnit(request.unit() != null ? request.unit() : "TRY");
        event.setMeta(request.meta());
        event.setTimestamp(LocalDateTime.now());

        // 2. Event'i kaydet
        Event savedEvent = eventRepository.save(event);

        // 3. Risk motorunu tetikle (async olarak da yapılabilir)
        riskEngineService.processEvent(savedEvent);

        // 4. Response döndür
        return mapToResponse(savedEvent);
    }

    @Override
    public Event getEventById(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event bulunamadı: " + eventId));
    }

    @Override
    public List<EventResponse> getEventsByUserId(String userId) {
        return eventRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getRecentEvents(int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24); // Son 24 saat
        return eventRepository.findRecentEvents(since)
                .stream()
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllOrderByTimestampDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Helper metodlar ---

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getUserId(),
                event.getService(),
                event.getEventType(),
                event.getAmount(),
                event.getUnit(),
                event.getMeta(),
                event.getTimestamp()
        );
    }
}