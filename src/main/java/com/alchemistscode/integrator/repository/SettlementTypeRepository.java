package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.commons.entity.catalog.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementTypeRepository extends JpaRepository<SettlementType, Integer> {
    SettlementType findByName(String name);
}
