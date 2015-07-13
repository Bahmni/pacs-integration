package org.bahmni.module.pacsintegration.integrationtest;

import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.BaseIntegrationTest;
import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertModalities.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertOrderTypes.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:truncateTables.sql")

})
@PrepareForTest(WebClientFactory.class)
public class EncounterFeedWorkerIT extends BaseIntegrationTest {
    @Mock
    private HttpClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EncounterFeedWorker encounterFeedWorker;

    @Before
    public void setUp() throws Exception {
        mockStatic(WebClientFactory.class);
        initMocks(this);

        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8080/encounter/1"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));
        when(webClient.get(new URI("http://localhost:8080/openmrs/ws/rest/v1/patient/105059a8-5226-4b1f-b512-0d3ae685287d"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/samplePatient.json"));
    }

    @Test
    public void shouldSendRadiologyOrderToAModality() throws Exception {
        encounterFeedWorker.process(new Event("event id", "/encounter/1"));

        List<Order> savedOrders = orderRepository.findAll();
        assertEquals(1, savedOrders.size());
        assertEquals("08d2dfa2-2274-44a1-a29e-30ea02df2798", savedOrders.get(0).getOrderUuid());
        assertEquals("HEAD Skull AP", savedOrders.get(0).getTestName());
        assertEquals("c42e71d7-3f10-11e4-adec-0800271c1b75", savedOrders.get(0).getTestUuid());

    }

    @Test
    public void shouldAddToFailedEventsWhenModalityIsNotAvailable() throws Exception {
        modalityStubServer.stop();
        try {
            encounterFeedWorker.process(new Event("event id", "/encounter/1"));
            Assert.fail("Should be throwing a exception since modality is down");
        } catch (RuntimeException e) {
            Assert.assertEquals("Failed send order to modality", e.getMessage());
            List<Order> savedOrders = orderRepository.findAll();
            assertEquals(0, savedOrders.size());
        }
    }

}