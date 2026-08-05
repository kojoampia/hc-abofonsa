package net.jojoaddison.abofonsa.preview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.repository.UserRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Who may reach what under {@code /api}.
 *
 * <p>This exists because of a real breach of exactly this rule. generator-jhipster seeds two
 * accounts, {@code admin} and {@code user}, each with a bcrypt hash of its own login committed to
 * the repository; {@code AdminAccountInitializer} rotates only the first. The security rules then
 * said {@code .requestMatchers("/api/**").authenticated()}, so {@code user}/{@code user} — a
 * credential printed in the generator's own documentation — could read every captured email address
 * and its opt-in token, and rewrite the pledge hand-off URL the launch page sends donors to.
 *
 * <p>The account is deleted and the rule is now {@code hasAuthority(ROLE_ADMIN)}. Neither fact is
 * self-evident from reading the config, and both are the sort of thing a regeneration undoes
 * silently, so they are asserted rather than trusted. A test that only checked the happy path would
 * have passed throughout the vulnerable period.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ApiAuthorizationIT {

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(strings = { "/api/waitlist-signups", "/api/capture-events", "/api/launch-settings", "/api/users" })
    void anonymousIsUnauthorized(String url) throws Exception {
        restMockMvc.perform(get(url)).andExpect(status().isUnauthorized());
    }

    /**
     * The regression that matters. A principal holding a session but not ROLE_ADMIN must be refused
     * everywhere, not merely under {@code /api/admin/**} — that narrower rule was in place while the
     * whole entity API was readable.
     */
    @ParameterizedTest
    @ValueSource(
        strings = {
            "/api/waitlist-signups",
            "/api/capture-events",
            "/api/data-export-logs",
            "/api/launch-settings",
            "/api/social-links",
            "/api/users",
            "/api/admin/metrics/summary",
            "/api/admin/export/waitlist.csv",
        }
    )
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void nonAdminIsForbidden(String url) throws Exception {
        restMockMvc.perform(get(url)).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = { "/api/waitlist-signups", "/api/capture-events", "/api/launch-settings", "/api/users" })
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void adminIsAllowed(String url) throws Exception {
        restMockMvc.perform(get(url)).andExpect(status().isOk());
    }

    /**
     * Writes too. A read-only check would pass against a configuration that still let a non-admin
     * repoint {@code launchSetting.fundUrl} at somebody else's payment page.
     */
    @Test
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    void nonAdminCannotWrite() throws Exception {
        restMockMvc.perform(post("/api/social-links").contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }

    /**
     * Account self-service is gone from {@code AccountResource}, and denied by URL as well, so that
     * a regeneration that puts the handlers back cannot open a way to create a second account.
     */
    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void accountSelfServiceIsDeniedEvenToAdmin() throws Exception {
        restMockMvc
            .perform(post("/api/register").contentType("application/json").content("{\"login\":\"x\",\"password\":\"password\"}"))
            .andExpect(status().isForbidden());
        restMockMvc.perform(get("/api/activate").param("key", "irrelevant")).andExpect(status().isForbidden());
        restMockMvc
            .perform(post("/api/account/reset-password/init").contentType("text/plain").content("x@example.com"))
            .andExpect(status().isForbidden());
    }

    /**
     * The seeded account itself. Liquibase deletes it (see
     * {@code 20260805110000_removed_seeded_user_account.xml}), and the test suite runs the same
     * changelogs the server does, so this asserts the migration rather than the security rules.
     */
    @Test
    void theSeededDemoAccountDoesNotExist() {
        assertThat(userRepository.findOneByLogin("user")).isEmpty();
    }
}
