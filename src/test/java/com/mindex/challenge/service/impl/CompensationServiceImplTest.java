package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
    private String compensationUrl;
    private String compensationIdUrl;
    private String compensationHistoryUrl;

    @Autowired
    private CompensationService compensationService;

    @MockBean
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
        compensationHistoryUrl = "http://localhost:" + port + "/compensation/{id}/history";
    }

    @Test
    public void testCreateRead() {
        Mockito.when(employeeService.exists(Mockito.anyString())).thenAnswer(params -> {
            String employeeId = params.getArgument(0);
            return "some employee".equals(employeeId);
        });

        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId("some employee");
        testCompensation.setSalary(100000);
        testCompensation.setEffectiveDate(LocalDate.now().minusDays(1));

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertNotNull(createdCompensation.getEmployeeId());
        assertCompensationEquivalence(testCompensation, createdCompensation);


        // Read current checks
        Compensation currentCompensation = restTemplate.getForEntity(
                compensationIdUrl,
                Compensation.class,
                createdCompensation.getEmployeeId()
        ).getBody();
        assertNotNull(currentCompensation);
        assertCompensationEquivalence(createdCompensation, currentCompensation);


        // Create some history
        Compensation oldCompensation = new Compensation();
        oldCompensation.setEmployeeId("some employee");
        oldCompensation.setSalary(90000);
        oldCompensation.setEffectiveDate(LocalDate.now().minusYears(2));

        restTemplate.postForEntity(compensationUrl, oldCompensation, Compensation.class).getBody();

        // Read history checks
        Compensation[] historyCompensation = restTemplate.getForEntity(
                compensationHistoryUrl,
                Compensation[].class,
                createdCompensation.getEmployeeId()
        ).getBody();

        assertNotNull(historyCompensation);
        assertEquals(2, historyCompensation.length);
        assertCompensationEquivalence(testCompensation, historyCompensation[0]);
        assertCompensationEquivalence(oldCompensation, historyCompensation[1]);
    }

    private void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
