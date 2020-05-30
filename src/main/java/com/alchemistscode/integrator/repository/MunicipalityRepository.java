package com.alchemistscode.integrator.repository;

import com.alchemistscode.sepomex.commons.entity.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MunicipalityRepository extends JpaRepository<Municipality, Integer> {
    Municipality findByStateIdAndName(Integer state, String name);
}
