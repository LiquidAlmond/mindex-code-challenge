package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.error.CannotCreateCompensation;
import com.mindex.challenge.error.EmployeeNotFound;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);

        Compensation persistedCompensation = compensationService.create(compensation);

        if (persistedCompensation == null) {
            throw new CannotCreateCompensation(compensation.getEmployeeId());
        }

        return persistedCompensation;
    }

    @GetMapping("/compensation/{id}")
    public Compensation readCurrent(@PathVariable String id) {
        LOG.debug("Received current compensation read request for id [{}]", id);

        Compensation compensation = compensationService.readCurrent(id);

        if (compensation == null) {
            throw new EmployeeNotFound(id);
        }

        return compensation;
    }

    /*
     * Not requested in the tasks, but demonstrates why I modeled Compensation the way I did.
     */
    @GetMapping("/compensation/{id}/history")
    public List<Compensation> readHistory(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        List<Compensation> compensations = compensationService.read(id);

        if (compensations == null) {
            throw new EmployeeNotFound(id);
        }

        return compensations;
    }
}
