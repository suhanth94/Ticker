package com.solactive.assignment.controller;


import com.solactive.assignment.businessobject.Tick;
import com.solactive.assignment.service.TickService;
import com.solactive.assignment.stats.TickStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;


@Controller
public class TickController {

    @Autowired
    private TickService tickService;

    @RequestMapping(value = "/ticks", method = RequestMethod.POST)
    public ResponseEntity<String> createTick(@RequestBody Tick tickInput){
        ZonedDateTime timer = ZonedDateTime.now(ZoneOffset.UTC);
        if(tickInput.getTimestamp() < (timer.toEpochSecond() - 60)*1000){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            tickService.saveTick(tickInput);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<TickStatistics> getStatistics() {
        return ResponseEntity.ok().body(tickService.fetchTickStatistics());
    }

    @RequestMapping(value = "/statistics/{instrument}", method = RequestMethod.GET)
    public ResponseEntity<TickStatistics> getStatistics(@PathVariable("instrument") String instrument) {
        return ResponseEntity.ok().body(tickService.fetchTickStatisticsInstrument(instrument));
    }

}
