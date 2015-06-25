package org.bahmni.pacsintegration.schedulers;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("openMRSEncounterFeedJob")
public class OpenMRSEncounterFeedJob implements Job{
    @Override
    public void process() {
        System.out.println("Yeah!!!" + new Date());
    }
}
