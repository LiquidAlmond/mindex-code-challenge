package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeIdUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @MockBean
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reporting-structure/{id}";
    }

    @Test
    public void testDirectReportCount() {
        Mockito.when(employeeService.read(Mockito.anyString())).thenAnswer(params -> {
            String employeeId = params.getArgument(0);

            if (!List.of("1", "2", "3", "4", "5", "6").contains(employeeId)) {
                return null;
            }

            Employee employee = new Employee();

            employee.setEmployeeId(employeeId);
            List<String> directReports = testReports.get(employeeId);

            if (directReports != null) {
                employee.setDirectReports(
                        directReports.stream().map(id -> {
                            Employee directReport = new Employee();
                            directReport.setEmployeeId(id);
                            return directReport;
                        }).toList()
                );
            }

            return employee;
        });
        ReportingStructure reporting;

        reporting = reportingStructureService.read("1");
        assertNotNull(reporting);
        assertEquals(5, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("2");
        assertNotNull(reporting);
        assertEquals(2, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("3");
        assertNotNull(reporting);
        assertEquals(1, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("4");
        assertNotNull(reporting);
        assertEquals(0, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("5");
        assertNotNull(reporting);
        assertEquals(0, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("6");
        assertNotNull(reporting);
        assertEquals(0, reporting.getNumberOfReports());

        reporting = reportingStructureService.read("7");
        assertNull(reporting);
    }

    private Map<String, List<String>> testReports = Map.ofEntries(
            Map.entry("1", List.of("2", "3")),
            Map.entry("2", List.of("4", "5")),
            Map.entry("3", List.of("6"))
    );
}
