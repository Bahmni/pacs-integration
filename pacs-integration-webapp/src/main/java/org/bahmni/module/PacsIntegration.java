package org.bahmni.module;

import org.bahmni.module.pacsintegration.repository.CronJobRepository;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
//@ComponentScan(basePackages = "org.bahmni.module.*")
@EnableTransactionManagement
public class PacsIntegration extends SpringBootServletInitializer {

    @Autowired
    CronJobRepository cronJobRepository;

    @RequestMapping("/")
    String home() {
        return "PACS Integration module is up and running.";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PacsIntegration.class, args);
    }

//    @Bean
//    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf) {
//        return hemf.getSessionFactory();
//    }
}
