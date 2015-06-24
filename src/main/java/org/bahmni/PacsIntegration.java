package org.bahmni;

import org.bahmni.pacsintegration.SchedulerConfig;
import org.bahmni.pacsintegration.model.Modality;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.repository.ModalityRepository;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@EnableAutoConfiguration
@Import({SchedulerConfig.class})
public class PacsIntegration {

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private ModalityRepository modalityRepository;

    @RequestMapping("/")
    List<OrderType> home() {
        Modality modality = new Modality(new Random().nextInt(), "Modality", "Some description", "http://google.com");
        modalityRepository.save(modality);
        orderTypeRepository.save(new OrderType(new Random().nextInt(), "noname", modality));
        return orderTypeRepository.findAll();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PacsIntegration.class, args);
    }
}
