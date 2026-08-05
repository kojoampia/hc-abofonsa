package net.jojoaddison.abofonsa.preview.management;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class SecurityMetersService {

    public static final String INVALID_TOKENS_METER_NAME = "security.authentication.invalid-tokens";
    public static final String INVALID_TOKENS_METER_DESCRIPTION =
        "Indicates validation error count of the tokens presented by the clients.";
    public static final String INVALID_TOKENS_METER_BASE_UNIT = "errors";
    public static final String INVALID_TOKENS_METER_CAUSE_DIMENSION = "cause";

    /**
     * Password attempts, as opposed to token problems.
     *
     * <p>The four counters above track what happens to a token that has already been issued. Nothing
     * counted a wrong password, so a sustained guessing campaign against the one account that can
     * read the waitlist raised no metric and wrote no log line — it was invisible from both ends.
     */
    public static final String AUTHENTICATION_FAILURES_METER_NAME = "security.authentication.failures";
    public static final String AUTHENTICATION_FAILURES_METER_DESCRIPTION = "Counts failed username/password authentication attempts.";
    public static final String AUTHENTICATION_FAILURES_METER_BASE_UNIT = "attempts";

    private final Counter tokenInvalidSignatureCounter;
    private final Counter tokenExpiredCounter;
    private final Counter tokenUnsupportedCounter;
    private final Counter tokenMalformedCounter;
    private final Counter authenticationFailureCounter;
    private final Counter authenticationThrottledCounter;

    public SecurityMetersService(MeterRegistry registry) {
        this.tokenInvalidSignatureCounter = invalidTokensCounterForCauseBuilder("invalid-signature").register(registry);
        this.tokenExpiredCounter = invalidTokensCounterForCauseBuilder("expired").register(registry);
        this.tokenUnsupportedCounter = invalidTokensCounterForCauseBuilder("unsupported").register(registry);
        this.tokenMalformedCounter = invalidTokensCounterForCauseBuilder("malformed").register(registry);
        this.authenticationFailureCounter = authenticationFailuresCounterForOutcomeBuilder("bad-credentials").register(registry);
        this.authenticationThrottledCounter = authenticationFailuresCounterForOutcomeBuilder("throttled").register(registry);
    }

    private Counter.Builder authenticationFailuresCounterForOutcomeBuilder(String outcome) {
        return Counter.builder(AUTHENTICATION_FAILURES_METER_NAME)
            .baseUnit(AUTHENTICATION_FAILURES_METER_BASE_UNIT)
            .description(AUTHENTICATION_FAILURES_METER_DESCRIPTION)
            .tag("outcome", outcome);
    }

    /** A password was presented and rejected. */
    public void trackAuthenticationFailure() {
        this.authenticationFailureCounter.increment();
    }

    /** A client was refused before its password was even checked, having failed too often. */
    public void trackAuthenticationThrottled() {
        this.authenticationThrottledCounter.increment();
    }

    private Counter.Builder invalidTokensCounterForCauseBuilder(String cause) {
        return Counter.builder(INVALID_TOKENS_METER_NAME)
            .baseUnit(INVALID_TOKENS_METER_BASE_UNIT)
            .description(INVALID_TOKENS_METER_DESCRIPTION)
            .tag(INVALID_TOKENS_METER_CAUSE_DIMENSION, cause);
    }

    public void trackTokenInvalidSignature() {
        this.tokenInvalidSignatureCounter.increment();
    }

    public void trackTokenExpired() {
        this.tokenExpiredCounter.increment();
    }

    public void trackTokenUnsupported() {
        this.tokenUnsupportedCounter.increment();
    }

    public void trackTokenMalformed() {
        this.tokenMalformedCounter.increment();
    }
}
