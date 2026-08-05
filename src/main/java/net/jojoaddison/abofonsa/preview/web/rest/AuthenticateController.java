package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.security.SecurityUtils.AUTHORITIES_CLAIM;
import static net.jojoaddison.abofonsa.preview.security.SecurityUtils.JWT_ALGORITHM;
import static net.jojoaddison.abofonsa.preview.security.SecurityUtils.USER_ID_CLAIM;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.management.SecurityMetersService;
import net.jojoaddison.abofonsa.preview.security.DomainUserDetailsService.UserWithId;
import net.jojoaddison.abofonsa.preview.service.RequestThrottleService;
import net.jojoaddison.abofonsa.preview.service.VisitorContextService;
import net.jojoaddison.abofonsa.preview.web.rest.vm.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RequestThrottleService requestThrottleService;

    private final VisitorContextService visitorContextService;

    private final SecurityMetersService securityMetersService;

    public AuthenticateController(
        JwtEncoder jwtEncoder,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        RequestThrottleService requestThrottleService,
        VisitorContextService visitorContextService,
        SecurityMetersService securityMetersService
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.requestThrottleService = requestThrottleService;
        this.visitorContextService = visitorContextService;
        this.securityMetersService = securityMetersService;
    }

    /**
     * {@code POST /authenticate} : exchange a password for a token.
     *
     * <p>Throttled and logged. Neither was true before: this endpoint accepted unlimited attempts
     * against the single account that can read every captured email address, and a wrong password
     * produced no metric and no log line, so guessing at it left no trace anywhere.
     *
     * <p>The client is identified by the same salted hash the rest of the application uses, which
     * since {@link VisitorContextService} was corrected means the address our own proxy asserts
     * rather than one the caller can put in a header.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        String clientKey = visitorContextService.ipHash(request);

        if (!requestThrottleService.loginAllowed(clientKey)) {
            securityMetersService.trackAuthenticationThrottled();
            LOG.warn(
                "Refused an authentication attempt for '{}' from a client that has failed too often [{}]",
                loginVM.getUsername(),
                clientKey
            );
            // 429 rather than 401. It is not a claim about the password — which was not checked —
            // and a client that is merely mistyping deserves to be told to wait rather than left
            // guessing at why a correct password stopped working.
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many failed sign-in attempts. Please try again later.");
        }

        var authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            int failures = requestThrottleService.recordLoginFailure(clientKey);
            securityMetersService.trackAuthenticationFailure();
            // The login is logged, the client hash is logged, the password is not. WARN because the
            // whole point is that this shows up in an ordinary log scan.
            LOG.warn("Failed authentication for '{}' [{}] — {} failure(s) in this window", loginVM.getUsername(), clientKey, failures);
            throw e;
        }

        // Somebody who mistypes nine times and then gets it right should not be left one attempt
        // from a lockout.
        requestThrottleService.clearLoginFailures(clientKey);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.createToken(authentication, loginVM.isRememberMe());
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)},
     * or with status {@code 401 (Unauthorized)} if not authenticated.
     */
    @GetMapping("/authenticate")
    public ResponseEntity<Void> isAuthenticated(Principal principal) {
        LOG.debug("REST request to check if the current user is authenticated");
        return ResponseEntity.status(principal == null ? HttpStatus.UNAUTHORIZED : HttpStatus.NO_CONTENT).build();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        var now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);
        if (authentication.getPrincipal() instanceof UserWithId user) {
            builder.claim(USER_ID_CLAIM, user.getId());
        }

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
