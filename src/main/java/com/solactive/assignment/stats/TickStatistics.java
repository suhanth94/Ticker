package com.solactive.assignment.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

@Component
public class TickStatistics {

    @JsonProperty
    private double avg;
    @JsonProperty
    private long count;
    @JsonProperty
    private double min;
    @JsonProperty
    private double max;


    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
