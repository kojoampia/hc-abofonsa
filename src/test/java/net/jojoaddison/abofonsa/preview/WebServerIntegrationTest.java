package net.jojoaddison.abofonsa.preview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.jojoaddison.abofonsa.preview.config.AsyncSyncConfiguration;
import net.jojoaddison.abofonsa.preview.config.DatabaseTestcontainer;
import net.jojoaddison.abofonsa.preview.config.JacksonConfiguration;
import net.jojoaddison.abofonsa.preview.config.JacksonHibernateConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * {@link IntegrationTest}, but on a real servlet container listening on a real port.
 *
 * <p>Almost everything here should use {@code @IntegrationTest} and MockMvc, which is faster and
 * needs no port. This exists for the few things MockMvc cannot see, because it stands in for the
 * container rather than running one: a {@code RequestDispatcher.forward}, for instance, is recorded
 * by MockMvc as an expectation and never actually performed — so nothing about the response the
 * container would really have written is observable. {@link
 * net.jojoaddison.abofonsa.preview.web.filter.SecurityHeadersIT} exists because a whole class of
 * missing security headers hid in exactly that gap.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { AbofonsaPreviewApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, JacksonHibernateConfiguration.class },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface WebServerIntegrationTest {}
