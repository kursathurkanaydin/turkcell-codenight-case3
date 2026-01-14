package com.example.demo.controller;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.service.IEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Event Controller
 * POST /events - Yeni event alır ve işler
 * GET /events - Tüm event'leri listeler
 * GET /events/{id} - Event detayı getirir
 * GET /events/user/{userId} - Kullanıcının event'lerini getirir
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final IEventService eventService;

    public EventController(IEventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Yeni event alır ve risk motorunu tetikler
     * POST /events
     */
    @PostMapping
    public ResponseEntity<EventResponse> processEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.processEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Tüm event'leri listeler
     * GET /events
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Son event'leri listeler
     * GET /events/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<EventResponse>> getRecentEvents(
            @RequestParam(defaultValue = "10") int limit) {
        List<EventResponse> events = eventService.getRecentEvents(limit);
        return ResponseEntity.ok(events);
    }

    /**
     * Kullanıcının event'lerini getirir
     * GET /events/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponse>> getEventsByUser(@PathVariable String userId) {
        List<EventResponse> events = eventService.getEventsByUserId(userId);
        return ResponseEntity.ok(events);
    }
}