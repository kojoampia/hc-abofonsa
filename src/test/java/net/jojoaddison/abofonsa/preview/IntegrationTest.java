package net.jojoaddison.abofonsa.preview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.jojoaddison.abofonsa.preview.config.AsyncSyncConfiguration;
import net.jojoaddison.abofonsa.preview.config.DatabaseTestcontainer;
import net.jojoaddison.abofonsa.preview.config.JacksonConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        AbofonsaPreviewApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        net.jojoaddison.abofonsa.preview.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
