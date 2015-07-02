package org.bahmni.module.pacsintegration.atomfeed.worker;

import org.bahmni.module.pacsintegration.atomfeed.BaseIntegrationTest;
import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.model.Orders;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertModalities.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertOrderTypes.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:truncateTables.sql")

})
public class EncounterFeedWorkerIT extends BaseIntegrationTest {
    @Mock
    private HttpClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EncounterFeedWorker encounterFeedWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldOnlySaveRadiologyOrdersInEncounterFeed() throws Exception {
        when(webClient.get(any(URI.class))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));

        encounterFeedWorker.setWebClient(webClient);
        encounterFeedWorker.setUrlPrefix("Prefix");
        encounterFeedWorker.process(new Event("event id", "/openmrs"));

        List<Orders> savedOrders = orderRepository.findAll();
        assertEquals(1, savedOrders.size());
        assertEquals("ac0819a9-11c1-4310-8f0a-feee71e5086b", savedOrders.get(0).getOrderUuid());
        assertEquals("Anaemia Panel", savedOrders.get(0).getTestName());
        assertEquals("4e167df9-80f5-4caa-9a82-ec97908cbd59", savedOrders.get(0).getTestUuid());
    }
}