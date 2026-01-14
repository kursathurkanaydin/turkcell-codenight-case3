package com.example.demo.service;

import com.example.demo.entity.ActionType;
import com.example.demo.service.model.ActionResult;
import com.example.demo.service.model.RuleHit;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ActionProcessor {

    public ActionResult process(List<RuleHit> hits) {
        if (hits == null || hits.isEmpty()) {
            return new ActionResult(null, List.of());
        }

        RuleHit selected = hits.stream()
                .min(Comparator.comparingInt(RuleHit::priority))
                .orElse(null);

        List<ActionType> suppressed = hits.stream()
                .filter(h -> selected == null || !h.ruleId().equals(selected.ruleId()))
                .map(RuleHit::action)
                .distinct()
                .toList();

        return new ActionResult(selected != null ? selected.action() : null, suppressed);
    }
}