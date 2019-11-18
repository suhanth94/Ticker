package com.solactive.assignment.businessobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

@Component
public class Tick {

    @JsonProperty
    private String instrument;
    @JsonProperty
    private double price;
    @JsonProperty
    private long timestamp;

    public String getInstrument() {
        return instrument;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
