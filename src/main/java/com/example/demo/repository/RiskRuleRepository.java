package com.example.demo.repository;

import com.example.demo.entity.RiskRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRule, String> {
    List<RiskRule> findByIsActiveTrue();
}