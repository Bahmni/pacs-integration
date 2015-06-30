package org.bahmni.pacsintegration.atomfeed;

import org.bahmni.PacsIntegration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PacsIntegration.class)
public abstract class BaseIntegrationTest {
}
