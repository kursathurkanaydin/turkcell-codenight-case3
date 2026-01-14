package com.example.demo.dto.mapper;

import com.example.demo.dto.request.CreateRiskRuleRequest;
import com.example.demo.dto.request.UpdateRiskRuleRequest;
import com.example.demo.dto.response.RiskRuleResponse;
import com.example.demo.entity.RiskRule;
import org.springframework.stereotype.Component;

@Component
public class RiskRuleMapper {


    public RiskRule toRiskRuleFromCreateRequest(CreateRiskRuleRequest request) {
        RiskRule riskRule = new RiskRule();
        riskRule.setRuleId(request.id());
        riskRule.setCondition(request.condition());
        riskRule.setAction(request.action());
        riskRule.setPriority(request.priority());
        riskRule.setActive(request.active());
        return riskRule;
    }

    public void updateRiskRuleFromRequest(UpdateRiskRuleRequest request, RiskRule riskRule){
        riskRule.setActive(request.active());
        riskRule.setPriority(request.priority());
        riskRule.setAction(request.action());
        riskRule.setCondition(request.condition());
    }


    public RiskRuleResponse toRiskRuleResponseFromRiskRule(RiskRule riskRule) {
        RiskRuleResponse response = new RiskRuleResponse(
                riskRule.getRuleId(),
                riskRule.getCondition(),
                riskRule.getAction(),
                riskRule.getPriority(),
                riskRule.getActive());

        return response;
    }
}