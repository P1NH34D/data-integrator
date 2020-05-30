package com.alchemistscode.integrator.wrapper;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class SepomexStatistics implements Serializable {
    private Date unloadDate;
    private String state;
    private Integer unloaded;
    private Integer registered;

    public String toString(){
        StringBuilder statistics = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        statistics.append("[").append(state).append("] ")
                .append(" Date: ").append(dateFormat.format(unloadDate))
                .append(" Loaded: ").append(unloaded)
                .append(" Registered: ").append(registered)
                .append(" Excluded: ").append(unloaded - registered);

        return statistics.toString();
    }
}