package com.example.demo.repository;

import com.example.demo.entity.CaseAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionServiceRepository extends JpaRepository<CaseAction, String> {
}
