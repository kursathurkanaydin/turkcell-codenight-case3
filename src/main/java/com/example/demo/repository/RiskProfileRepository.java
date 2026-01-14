package com.example.demo.repository;

import com.example.demo.entity.RiskProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskProfileRepository extends JpaRepository<RiskProfile, String> {
    Optional<RiskProfile> findByUserId(String userId);
}