package com.alchemistscode.integrator.wrapper;

import com.alchemistscode.sepomex.commons.entity.Settlement;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class SepomexUploadInfo implements Serializable {
    private static final long serialVersionUID = -3315228551286846809L;

    private String id;
    private Date processStart;
    private Date loadEnd;
    private Date processEnd;
    private Integer uploaded;
    private Integer registered;

    private String oldState;
    private String newState;
    private Integer stateId;
    private Integer settlementByState;

    private String oldMunicipality;
    private String newMunicipality;
    private Integer municipalityId;

    private List<SepomexStatistics> statistics;
    private List<Settlement> settlements;

    public String toString() {
        StringBuilder sepomex = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sepomex.append("id:").append(id).append(", ");
        sepomex.append("processStart:").append(dateFormat.format(processStart)).append(", ");
        sepomex.append("uploaded:").append(uploaded).append(", ");
        sepomex.append("registered:").append(registered).append(", ");
        if (loadEnd != null)
            sepomex.append("loadEnd:").append(dateFormat.format(loadEnd)).append(", ");
        if (processEnd != null)
            sepomex.append("processEnd:").append(dateFormat.format(processEnd));
        return sepomex.toString();
    }
}
