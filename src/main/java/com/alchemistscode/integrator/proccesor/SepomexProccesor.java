package com.alchemistscode.integrator.proccesor;

import com.alchemistscode.integrator.repository.MunicipalityRepository;
import com.alchemistscode.integrator.repository.SettlementRepository;
import com.alchemistscode.integrator.repository.SettlementTypeRepository;
import com.alchemistscode.integrator.repository.ZoneRepository;
import com.alchemistscode.integrator.util.SepomexUtil;
import com.alchemistscode.integrator.wrapper.SepomexStatistics;
import com.alchemistscode.integrator.wrapper.SepomexUploadInfo;
import com.alchemistscode.sepomex.entity.Municipality;
import com.alchemistscode.sepomex.entity.Settlement;
import com.alchemistscode.sepomex.entity.catalog.SettlementType;
import com.alchemistscode.sepomex.entity.catalog.Zone;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
public class SepomexProccesor implements Processor {
    @Autowired
    private SepomexUtil util;

    @Autowired
    private SettlementTypeRepository settlementTypeRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        byte[] fileBuffer = exchange.getIn().getBody(byte[].class);
        SepomexUploadInfo uploadInfo = (SepomexUploadInfo) exchange.getIn().getHeader("information");
        uploadInfo.setUploaded(0);
        uploadInfo.setRegistered(0);

        try (InputStream is = new InputStreamCache(fileBuffer);
             HSSFWorkbook workbook = (HSSFWorkbook) WorkbookFactory.create(is)
        ) {
            Iterator<Sheet> itSheet = workbook.sheetIterator();
            while (itSheet.hasNext()) {
                Sheet sheet = itSheet.next();
                String stateName = sheet.getSheetName().replace("_", " ");
                stateName = stateName.equals("Distrito Federal") ? "Ciudad de México" : stateName;
                uploadInfo.setNewState(stateName);
                if (!stateName.equals("Nota")) {
                    processState(sheet, uploadInfo);

                }
            }
        } catch (IOException ioex) {
            log.error("Error: {}", ioex.getCause());
        }
    }

    private void processState(Sheet sheet, SepomexUploadInfo uploadInfo) {
        Set<Settlement> settlementsByStates = new HashSet<>();
        Integer upload = 0;
        Integer register = 0;

        Iterator<Row> itRow = sheet.rowIterator();
        while (itRow.hasNext()) {
            Row row = itRow.next();
            if (row.getRowNum() > 0 && validateRow(row)) {
                String state = row.getCell(3).getStringCellValue();
                uploadInfo.setNewMunicipality(state);
                util.validateInfo(uploadInfo);
                upload++;
                Settlement settlement = createSettlement(uploadInfo, row);
                try {
                    Integer settlementId = settlementRepository.exists(settlement.getZipCode(), settlement.getName(),
                            settlement.getMunicipality().getId(), settlement.getSettlementType().getId(), settlement.getZone().getId());

                    boolean exists = settlementsByStates.stream()
                            .anyMatch(s -> s.hashCode() == settlement.hashCode());

                    if ((settlementId == null || settlementId == 0) && !exists) {
                        settlementsByStates.add(settlement);
                        register++;
                    }
                } catch (Exception exception) {
                    log.info("Duplicate: {}", settlement);
                }
            }
        }
        uploadInfo.setUploaded(uploadInfo.getUploaded() + upload);
        uploadInfo.setRegistered(uploadInfo.getRegistered() + register);
        uploadInfo.getSettlements().addAll(settlementsByStates);
        uploadInfo.setSettlementByState(settlementsByStates.size());

        SepomexStatistics statistics = SepomexStatistics.builder()
                .state(uploadInfo.getOldState())
                .unloadDate(new Date())
                .unloaded(upload)
                .registered(register)
                .build();
        uploadInfo.getStatistics().add(statistics);

        log.info("State processed: {}", statistics);
    }

    private Settlement createSettlement(SepomexUploadInfo uploadInfo, Row row) {
        SettlementType settlementType = settlementTypeRepository.findByName(row.getCell(2).getStringCellValue());
        Zone zone = zoneRepository.findByName(row.getCell(13).getStringCellValue());
        Municipality municipality = municipalityRepository.findById(uploadInfo.getMunicipalityId())
                .orElse(new Municipality());

        Settlement settlement = new Settlement();

        settlement.setName(row.getCell(1).getStringCellValue());
        settlement.setZipCode(row.getCell(0).getStringCellValue());
        settlement.setZone(zone);
        settlement.setMunicipality(municipality);
        settlement.setSettlementType(settlementType);

        return settlement;
    }

    private boolean validateRow(Row row) {
        return !row.getCell(0).getStringCellValue().isEmpty()                                                   // Código Postal
                && !row.getCell(1).getStringCellValue().isEmpty()                                               // Asentamiento
                && !row.getCell(2).getStringCellValue().isEmpty()                                               // Tipo Asentamiento
                && !row.getCell(3).getStringCellValue().isEmpty()                                               // Municipio
                && !row.getCell(13).getStringCellValue().isEmpty()                                              // Tipo Zona
                ;
    }

}
