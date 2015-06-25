package org.bahmni;

import org.bahmni.pacsintegration.model.Modality;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.model.QuartzScheduler;
import org.bahmni.pacsintegration.repository.ModalityRepository;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.bahmni.pacsintegration.repository.QuartzSchedulerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.bahmni")
public class PacsIntegration {

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private ModalityRepository modalityRepository;

    @Autowired
    QuartzSchedulerRepository quartzSchedulerRepository;

    @RequestMapping("/")
    List<QuartzScheduler> home() {
        return quartzSchedulerRepository.findAll();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PacsIntegration.class, args);
    }
}
