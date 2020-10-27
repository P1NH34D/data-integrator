package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.entity.catalog.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    Zone findByName(String name);
}
