package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureControllerTest {
    private String reportingStructureUrl;

    @MockBean
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reporting-structure/{id}";

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
    }

    @Test
    public void testRead() {
        ReportingStructure reportingStructure;

        // doing a couple tests using the rest template rather than being exhaustive
        reportingStructure = restTemplate.getForEntity(
                reportingStructureUrl,
                ReportingStructure.class,
                "1"
        ).getBody();

        assertNotNull(reportingStructure);
        assertEquals(5, reportingStructure.getNumberOfReports());

        reportingStructure = restTemplate.getForEntity(
                reportingStructureUrl,
                ReportingStructure.class,
                "6"
        ).getBody();

        assertNotNull(reportingStructure);
        assertEquals(0, reportingStructure.getNumberOfReports());

        // employee not defined in mock, need to check edge cases
        ResponseEntity<ReportingStructure> responseEntity = restTemplate.getForEntity(
                reportingStructureUrl,
                ReportingStructure.class,
                "7"
        );
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    final private Map<String, List<String>> testReports = Map.ofEntries(
            Map.entry("1", List.of("2", "3")),
            Map.entry("2", List.of("4", "5")),
            Map.entry("3", List.of("6"))
    );
}
