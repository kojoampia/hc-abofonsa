package net.jojoaddison.abofonsa.preview.config;

import net.jojoaddison.abofonsa.preview.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resets the seeded {@code admin} account's password from configuration at boot.
 *
 * <p>JHipster ships {@code users.csv} with a committed bcrypt hash of the password "admin". That is
 * fine for a dev database and unacceptable anywhere the dashboard is reachable, but the fix cannot
 * be "commit a different hash" — the hash would still be in the repository, and rotating it would
 * mean editing an applied Liquibase changelog, which Flyway-style checksumming forbids.
 *
 * <p>So the password lives in {@code ABOFONSA_ADMIN_PASSWORD} and is applied here instead. When the
 * variable is unset the seeded credential is left alone and a warning is logged, which keeps
 * {@code ./mvnw spring-boot:run} working out of the box for a developer while making the gap loud.
 */
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminAccountInitializer.class);
    private static final String ADMIN_LOGIN = "admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String configuredPassword;

    public AdminAccountInitializer(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("${abofonsa.admin.password:}") String configuredPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.configuredPassword = configuredPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (configuredPassword == null || configuredPassword.isBlank()) {
            LOG.warn(
                "ABOFONSA_ADMIN_PASSWORD is not set — the '{}' account still has its seeded development " +
                    "password. Set it before exposing the admin dashboard.",
                ADMIN_LOGIN
            );
            return;
        }

        userRepository.findOneByLogin(ADMIN_LOGIN).ifPresentOrElse(
            admin -> {
                // Re-encoding an unchanged password still produces a different hash (bcrypt salts
                // per call), so this cannot be skipped by comparing hashes — matches() is the only
                // way to avoid a pointless write on every restart.
                if (passwordEncoder.matches(configuredPassword, admin.getPassword())) {
                    LOG.debug("Admin password already matches the configured value; nothing to do.");
                    return;
                }
                admin.setPassword(passwordEncoder.encode(configuredPassword));
                userRepository.save(admin);
                LOG.info("Reset the '{}' account password from configuration.", ADMIN_LOGIN);
            },
            () -> LOG.error("No '{}' account found to configure — check the Liquibase user seed.", ADMIN_LOGIN)
        );
    }
}
