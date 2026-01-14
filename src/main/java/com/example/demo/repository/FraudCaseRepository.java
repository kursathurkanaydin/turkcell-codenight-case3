package com.example.demo.repository;

import com.example.demo.entity.CaseStatus;
import com.example.demo.entity.FraudCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudCaseRepository extends JpaRepository<FraudCase, String> {
    List<FraudCase> findByStatus(CaseStatus status);
}