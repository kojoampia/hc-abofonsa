package net.jojoaddison.abofonsa.preview.config;

import static org.springframework.security.config.Customizer.withDefaults;

import net.jojoaddison.abofonsa.preview.security.*;
import net.jojoaddison.abofonsa.preview.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    // A year, and deliberately *without* includeSubDomains. The apex is shared:
                    // web.abofonsa.com and fund.abofonsa.com are separate applications on the same
                    // host, and asserting a policy on somebody else's subdomain from here would take
                    // one of them offline for a year if it ever answered on plain HTTP — with no way
                    // to retract it from a browser that had already seen the header. Add it, and
                    // preload, once every subdomain is known to be HTTPS-only.
                    //
                    // Only emitted for a request Spring believes is secure, which in production
                    // depends on server.forward-headers-strategy being set. See application-prod.yml.
                    .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31_536_000).includeSubDomains(false).preload(false))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers("/index.html", "/*.js", "/*.txt", "/*.json", "/*.map", "/*.css").permitAll()
                    .requestMatchers("/*.ico", "/*.png", "/*.svg", "/*.webapp").permitAll()
                    .requestMatchers("/content/**").permitAll()
                    .requestMatchers("/resources/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/authenticate").permitAll()
                    // The launch page itself: content, waitlist capture, opt-in confirmation and the
                    // analytics beacon. Anonymous by definition — see PublicContentResource and
                    // PublicWaitlistResource for the rate limiting and bot checks that replace auth here.
                    .requestMatchers("/api/public/**").permitAll()
                    // JHipster's self-service account endpoints are gone from AccountResource, not merely
                    // unpermitted. This is the belt to that pair of braces: if a future regeneration puts
                    // them back, they answer 403 rather than quietly becoming reachable to anyone holding
                    // a session. There is one account here, seeded by Liquibase and given its password by
                    // AdminAccountInitializer; nothing should be able to create a second.
                    .requestMatchers("/api/register", "/api/activate", "/api/account/reset-password/**").denyAll()
                    // The signed-in operator's own account. Not ROLE_ADMIN only because "change my own
                    // password" is not an administrative act — it is the one thing any principal must be
                    // able to do for itself.
                    .requestMatchers("/api/account", "/api/account/**").authenticated()
                    // Everything else behind /api is administrative. `authenticated()` used to be enough
                    // here, which meant "holds any session" and "may read every captured email address"
                    // were the same permission — so generator-jhipster's seeded `user`/`user` account was
                    // a straight route to the waitlist table and to the content the public page renders.
                    // That account is deleted (see 20260805110000_removed_seeded_user_account.xml); this
                    // is what stops the next one mattering. Each generated resource repeats the rule as a
                    // class-level @Secured, so re-running the entity generator cannot widen it either.
                    .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/api/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/v3/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers("/management/health").permitAll()
                    .requestMatchers("/management/health/**").permitAll()
                    .requestMatchers("/management/info").permitAll()
                    .requestMatchers("/management/prometheus").permitAll()
                    .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
