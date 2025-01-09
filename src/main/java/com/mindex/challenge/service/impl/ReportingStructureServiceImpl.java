package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Reading reporting structure with id [{}]", id);

        Employee employee = employeeService.read(id);

        if (employee == null) {
            return null;
        }

        ReportingStructure reportingStructure = new ReportingStructure();

        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(this.findDirectReportCount(employee.getEmployeeId()));

        return reportingStructure;
    }

    /*
     * Typical breadth-first search.
     */
    private Integer findDirectReportCount(String employeeId) {
        Queue<String> unprocessedIds = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        unprocessedIds.offer(employeeId);

        while (!unprocessedIds.isEmpty()) {
            String currentId = unprocessedIds.poll();

            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);

            Employee emp = employeeService.read(currentId);

            if (emp == null) {
                continue;
            }

            List<Employee> directReports = emp.getDirectReports();

            if (directReports == null) {
                continue;
            }

            for (Employee directReport : directReports) {
                String reportId = directReport.getEmployeeId();
                if (reportId != null && !visited.contains(reportId)) {
                    unprocessedIds.offer(reportId);
                }
            }
        }

        return visited.size() - 1;
    }

}
