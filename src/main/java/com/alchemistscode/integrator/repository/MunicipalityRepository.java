package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.entity.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MunicipalityRepository extends JpaRepository<Municipality, Integer> {
    Municipality findByStateIdAndName(Integer state, String name);
}
