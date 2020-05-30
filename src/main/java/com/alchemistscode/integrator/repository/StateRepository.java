package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.commons.entity.catalog.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Integer> {
    State findByName(String name);
}
