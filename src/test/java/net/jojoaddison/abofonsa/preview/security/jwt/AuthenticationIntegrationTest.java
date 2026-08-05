package net.jojoaddison.abofonsa.preview.security.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.jojoaddison.abofonsa.preview.config.SecurityConfiguration;
import net.jojoaddison.abofonsa.preview.config.SecurityJwtConfiguration;
import net.jojoaddison.abofonsa.preview.config.WebConfigurer;
import net.jojoaddison.abofonsa.preview.management.SecurityMetersService;
import net.jojoaddison.abofonsa.preview.service.RequestThrottleService;
import net.jojoaddison.abofonsa.preview.service.VisitorContextService;
import net.jojoaddison.abofonsa.preview.web.rest.AuthenticateController;
import org.springframework.boot.test.context.SpringBootTest;
import tech.jhipster.config.JHipsterProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    properties = {
        "jhipster.security.authentication.jwt.base64-secret=fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8",
        "jhipster.security.authentication.jwt.token-validity-in-seconds=60000",
    },
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        AuthenticateController.class,
        // AuthenticateController throttles and logs failed password attempts, so this slice has to
        // carry the two collaborators that make that possible. A slice listing its beans by hand
        // fails to start the moment a controller gains a dependency, and the resulting error names
        // the context rather than the missing bean.
        RequestThrottleService.class,
        VisitorContextService.class,
        JwtAuthenticationTestUtils.class,
    }
)
public @interface AuthenticationIntegrationTest {}
