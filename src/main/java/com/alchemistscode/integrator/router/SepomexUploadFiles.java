package com.alchemistscode.integrator.router;

import com.alchemistscode.integrator.config.RoutesProperties;
import com.alchemistscode.integrator.proccesor.SepomexProccesor;
import com.alchemistscode.integrator.wrapper.SepomexStatistics;
import com.alchemistscode.integrator.wrapper.SepomexUploadInfo;
import com.alchemistscode.sepomex.commons.entity.Settlement;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class SepomexUploadFiles extends RouteBuilder {
    private final RoutesProperties props;

    @Value("${camel.batch.max.records}")
    private int maxRecords;

    @Value("${camel.batch.timeout}")
    private long batchTimeout;

    @Autowired
    private SepomexProccesor processor;

    private static final String TAG_INFORMATION = "information";
    private static final String TAG_DURATION = "duration";

    @Autowired
    public SepomexUploadFiles(RoutesProperties props) {
        this.props = props;
    }

    @Override
    public void configure() throws Exception {
        from(props.getRoutes().get("sepomex-in"))
                .log("File Upload Sepomex started ......")
                .process(exchange ->
                    exchange.getIn().setHeader(TAG_INFORMATION, SepomexUploadInfo.builder()
                            .id(UUID.randomUUID().toString())
                            .processStart(new Date())
                            .statistics(new ArrayList<>())
                            .settlements(new ArrayList<>())
                            .build())
                )
                .process(processor)
                .process(exchange -> {
                    SepomexUploadInfo info = (SepomexUploadInfo) exchange.getIn().getHeader(TAG_INFORMATION);
                    info.setLoadEnd(new Date());
                    exchange.getIn().setHeader("durationLoad", TimeUnit.MILLISECONDS.toSeconds(info.getLoadEnd().getTime() - info.getProcessStart().getTime()));
                    exchange.getIn().setBody(info.getSettlements());
                })
                .log("info: ${header.information}")
                .log("File Upload Sepomex finished  ${header.durationLoad} seg.")
                .transacted()
                .split(body()).streaming()
                .process(exchange -> {
                    Settlement settlement = exchange.getIn().getBody(Settlement.class);
                    Map<String, Object> asentamiento = new HashMap<>();
                    asentamiento.put("name", settlement.getName());
                    asentamiento.put("zipCode", settlement.getZipCode());
                    asentamiento.put("municipality", settlement.getMunicipality().getId());
                    asentamiento.put("settlementType", settlement.getSettlementType().getId());
                    asentamiento.put("zone", settlement.getZone().getId());

                    exchange.getIn().setBody(asentamiento);
                })
                .to("sql:INSERT INTO settlement(name, zipCode, municipality, settlementType, zone) " +
                        "VALUES (:#name, :#zipCode, :#municipality, :#settlementType, :#zone)?dataSource=#sepomexDS&batch=true")
                .end()
                .process(exchange -> {
                    SepomexUploadInfo info = (SepomexUploadInfo) exchange.getIn().getHeader(TAG_INFORMATION);
                    info.setProcessEnd(new Date());
                    exchange.getIn().setHeader("durationRegister", TimeUnit.MILLISECONDS.toSeconds(info.getProcessEnd().getTime() - info.getLoadEnd().getTime()));
                    exchange.getIn().setHeader(TAG_DURATION, TimeUnit.MILLISECONDS.toSeconds(info.getProcessEnd().getTime() - info.getProcessStart().getTime()));
                })
                .log("Register finished  ${header.durationRegister} seg.")
                .process(exchange -> {
                    SepomexUploadInfo info = (SepomexUploadInfo) exchange.getIn().getHeader(TAG_INFORMATION);
                    Map<String, Object> information = new HashMap<>();

                    information.put("id", info.getId());
                    information.put("service", "Sepomex");
                    information.put("create", info.getProcessStart());
                    information.put(TAG_DURATION, exchange.getIn().getHeader(TAG_DURATION));
                    information.put("upload", info.getUploaded());
                    information.put("register", info.getRegistered());
                    information.put("exclude", info.getUploaded() - info.getRegistered());

                    exchange.getIn().setBody(information);
                })
                .to("sql:INSERT INTO integrator_info(id, service, createAt, duration, uploaded, registered, excluded) " +
                        "VALUES (:#id,:#service,:#create,:#duration,:#upload,:#register,:#exclude)?dataSource=#integratorDS")
                .end()
                .process(exchange -> {
                    SepomexUploadInfo info = (SepomexUploadInfo) exchange.getIn().getHeader(TAG_INFORMATION);
                    exchange.getIn().setBody(info.getStatistics());
                })
                .split(body()).streaming()
                .process(exchange -> {
                    SepomexUploadInfo info = (SepomexUploadInfo) exchange.getIn().getHeader(TAG_INFORMATION);
                    SepomexStatistics statistics = exchange.getIn().getBody(SepomexStatistics.class);
                    Map<String, Object> statistic = new HashMap<>();

                    statistic.put("integratorId", info.getId());
                    statistic.put("state", statistics.getState());
                    statistic.put("create", statistics.getUnloadDate());
                    statistic.put("upload", statistics.getUnloaded());
                    statistic.put("register", statistics.getRegistered());
                    statistic.put("exclude", statistics.getUnloaded() - statistics.getRegistered());
                    exchange.getIn().setBody(statistic);
                })
                .to("sql:INSERT INTO datail_sepomex (info_id, state, createAt, uploaded, registered, excluded)  " +
                        "VALUES (:#integratorId,:#state,:#create,:#upload,:#register,:#exclude)?dataSource=#integratorDS")
                .end()
                .log("Summary ${header.information}")
                .log("Process Sepomex finished  ${header.duration} seg.")
                .end();
    }


}
