package com.example.demo.service;

import com.example.demo.dto.mapper.RiskRuleMapper;
import com.example.demo.dto.request.CreateRiskRuleRequest;
import com.example.demo.dto.request.UpdateRiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.entity.RiskRule;
import com.example.demo.repository.RiskRuleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiskRuleService {
    private final RiskRuleRepository riskRuleRepository;
    private final RiskRuleMapper riskRuleMapper;

    public RiskRuleService(RiskRuleRepository riskRuleRepository, RiskRuleMapper riskRuleMapper) {
        this.riskRuleRepository = riskRuleRepository;
        this.riskRuleMapper = riskRuleMapper;
    }


    public List<RiskRuleResponse> getAll() {
        return riskRuleRepository.findAll().stream()
                .map(riskRuleMapper::toRiskRuleResponseFromRiskRule)
                .collect(Collectors.toList());
    }
    public RiskRuleResponse createRiskRule(CreateRiskRuleRequest request){
        RiskRule riskRule = riskRuleMapper.toRiskRuleFromCreateRequest(request);
        RiskRule savedRiskRule = riskRuleRepository.save(riskRule);
        return riskRuleMapper.toRiskRuleResponseFromRiskRule(savedRiskRule);
    }

    public RiskRuleResponse updateRiskRule(UpdateRiskRuleRequest request, String id){
        RiskRule riskRule = riskRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        riskRuleMapper.updateRiskRuleFromRequest(request, riskRule);
        RiskRule updatedRiskRule = riskRuleRepository.save(riskRule);
        return riskRuleMapper.toRiskRuleResponseFromRiskRule(updatedRiskRule);
    }

    public RiskRuleResponse getRiskRule(String id){
        return riskRuleRepository.findById(id).map(riskRuleMapper::toRiskRuleResponseFromRiskRule).orElseThrow();
    }
}