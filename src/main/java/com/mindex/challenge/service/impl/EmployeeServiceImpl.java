package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());

        // We were previously returning what we were given instead of
        // what was saved.
        return employeeRepository.insert(employee);
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        return employeeRepository.findByEmployeeId(id);
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    // I could probably just use the read method just as well and check for
    // null, but decided this reads better in the calling method.
    @Override
    public boolean exists(String id) {
        LOG.debug("Checking if employee with id [{}] exists", id);
        Employee searchEmployee = new Employee();
        searchEmployee.setEmployeeId(id);
        return employeeRepository.exists(Example.of(searchEmployee));
    }
}
