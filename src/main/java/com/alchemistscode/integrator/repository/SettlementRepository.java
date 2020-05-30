package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.commons.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
    @Query("SELECT s.id FROM Settlement s WHERE s.zipCode = ?1 and s.name = ?2 and s.municipality.id = ?3 " +
            "and s.settlementType.id = ?4 and s.zone.id = ?5")
    Integer exists(String zipCode, String settlement, Integer municipality, Integer settlementType, Integer zone);

}
