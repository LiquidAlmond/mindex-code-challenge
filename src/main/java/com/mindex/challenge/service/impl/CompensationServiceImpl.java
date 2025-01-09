package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating employee compensation [{}]", compensation);

        if (!employeeService.exists(compensation.getEmployeeId())) {
            return null;
        }

        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation readCurrent(String id) {
        LOG.debug("Reading current compensation for employee id [{}]", id);

        // I debated with myself whether I should use a stream or a standard loop. Given
        // the simplicity of our data, I opted for the only slightly less efficient but
        // more readable (imho) method.
        return compensationRepository.findByEmployeeId(id)
                .stream()
                .filter(compensation -> compensation.getEffectiveDate().isBefore(LocalDate.now()))
                .max(Comparator.comparing(Compensation::getEffectiveDate))
                .orElse(null);
    }

    @Override
    public List<Compensation> read(String id) {
        LOG.debug("Reading all compensation for employee id [{}]", id);

        // Same debate and decision as above.
        return compensationRepository.findByEmployeeId(id)
                .stream()
                .sorted(Comparator.comparing(Compensation::getEffectiveDate).reversed())
                .toList();
    }
}
