package com.alchemistscode.integrator.util;

import com.alchemistscode.integrator.repository.MunicipalityRepository;
import com.alchemistscode.integrator.repository.StateRepository;
import com.alchemistscode.integrator.wrapper.SepomexUploadInfo;
import com.alchemistscode.sepomex.commons.entity.Municipality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SepomexUtil {
    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private StateRepository stateRepository;

    public void validateInfo(SepomexUploadInfo uploadInfo) {
        if (uploadInfo.getNewState() != null && !uploadInfo.getNewState().equals(uploadInfo.getOldState())) {
            uploadInfo.setStateId(stateRepository.findByName(uploadInfo.getNewState()).getId());
            uploadInfo.setOldState(uploadInfo.getNewState());
            uploadInfo.setSettlementByState(0);
        }
        if (uploadInfo.getNewMunicipality() != null
                && !uploadInfo.getNewMunicipality().equals(uploadInfo.getOldMunicipality())) {

            uploadInfo.setMunicipalityId(getMunicipalityId(uploadInfo.getStateId(), uploadInfo.getNewMunicipality()));
            uploadInfo.setOldMunicipality(uploadInfo.getNewMunicipality());
        }
    }

    private Integer getMunicipalityId(Integer stateId, String name) {
        try {
            Municipality municipality = municipalityRepository.findByStateIdAndName(stateId, name);
            return municipality.getId();
        } catch (Exception ex) {
            if (name != null && !name.isEmpty()) {
                Municipality newMunicipality = municipalityRepository.save(new Municipality(name, stateId));
                return newMunicipality.getId();
            }
            log.error("Error undefined Id municipaliti stete: {} municpality: {}", stateId, name);
            return 0;
        }
    }

}
