package com.example.demo.controller;

import com.example.demo.dto.request.CreateRiskRuleRequest;
import com.example.demo.dto.request.UpdateRiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.service.RiskRuleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/riskrules")
public class RiskRuleController {
    private final RiskRuleService riskRuleService;

    public RiskRuleController(RiskRuleService riskRuleService) {
        this.riskRuleService = riskRuleService;
    }

    @GetMapping()
    public List<RiskRuleResponse> getRiskRules(){
        return riskRuleService.getAll();
    }

    @GetMapping("/riskrule/")
    public RiskRuleResponse getRiskRule(@RequestParam String id){
        return riskRuleService.getRiskRule(id);

    }

    @PostMapping("/create/riskrule")
    public RiskRuleResponse createRiskRule(@Valid @RequestBody CreateRiskRuleRequest request){
        return riskRuleService.createRiskRule(request);
    }

    @PostMapping("/update/riskrule")
    public RiskRuleResponse updateRiskRule(@Valid @RequestBody UpdateRiskRuleRequest request){
        return riskRuleService.updateRiskRule(request, request.id());
    }
}
