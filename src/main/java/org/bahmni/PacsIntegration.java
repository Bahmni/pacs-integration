package org.bahmni;

import org.bahmni.pacsintegration.model.CronJob;
import org.bahmni.pacsintegration.repository.ModalityRepository;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.bahmni.pacsintegration.repository.CronJobRepository;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.bahmni")
public class PacsIntegration {

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private ModalityRepository modalityRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @RequestMapping("/")
    List<CronJob> home() {
        return cronJobRepository.findAll();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PacsIntegration.class, args);
    }

    @Bean
    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf) {
        return hemf.getSessionFactory();
    }
}
