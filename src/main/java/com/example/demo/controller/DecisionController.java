package com.example.demo.controller;

import com.example.demo.dto.response.DecisionResponse;
import com.example.demo.service.IDecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Decision Controller
 * GET /decisions - Tüm kararları listeler
 * GET /decisions/recent - Son kararları listeler
 * GET /decisions/{decisionId} - Karar detayı getirir
 * GET /decisions/user/{userId} - Kullanıcının kararlarını listeler
 */
@RestController
@RequestMapping("/decisions")
public class DecisionController {

    private final IDecisionService decisionService;

    public DecisionController(IDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /**
     * Tüm kararları listeler
     * GET /decisions
     */
    @GetMapping
    public ResponseEntity<List<DecisionResponse>> getAllDecisions() {
        List<DecisionResponse> decisions = decisionService.getAllDecisions();
        return ResponseEntity.ok(decisions);
    }

    /**
     * Son kararları listeler
     * GET /decisions/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<DecisionResponse>> getRecentDecisions(
            @RequestParam(defaultValue = "10") int limit) {
        List<DecisionResponse> decisions = decisionService.getRecentDecisions(limit);
        return ResponseEntity.ok(decisions);
    }

    /**
     * Karar detayı getirir
     * GET /decisions/{decisionId}
     */
    @GetMapping("/{decisionId}")
    public ResponseEntity<DecisionResponse> getDecisionById(@PathVariable String decisionId) {
        DecisionResponse decision = decisionService.getDecisionById(decisionId);
        return ResponseEntity.ok(decision);
    }

    /**
     * Kullanıcının kararlarını listeler
     * GET /decisions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DecisionResponse>> getDecisionsByUser(@PathVariable String userId) {
        List<DecisionResponse> decisions = decisionService.getDecisionsByUserId(userId);
        return ResponseEntity.ok(decisions);
    }
}