package net.jojoaddison.abofonsa.preview.management;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * What the waitlist is doing, as counters.
 *
 * <p>The rollup tables and the admin dashboard already answer "how many signups" precisely, and they
 * remain the source of truth — they are recomputable from {@code capture_event}, which these are not.
 * These exist for a different question: whether the funnel is working *right now*, at a glance,
 * beside the JVM and HTTP graphs, without opening the application.
 *
 * <p>The distinction matters most in the failure everyone actually has. A launch page whose signups
 * quietly drop to zero because the honeypot became over-eager, or because the confirmation mail
 * stopped sending, looks perfectly healthy on request-rate and error-rate: every response is a 200.
 * `rejected` and `throttled` climbing while `accepted` falls is what makes that visible.
 *
 * <p>These reach Grafana through the OpenTelemetry Java agent, which bridges Micrometer to OTLP —
 * so they arrive in Mimir alongside the agent's own JVM and HTTP metrics, with no scrape involved.
 * Deliberately low cardinality: an outcome tag with a handful of values and nothing per-address,
 * per-campaign or per-IP, because a metric label is stored per series forever.
 */
@Component
public class WaitlistMetrics {

    public static final String SIGNUPS_METER = "abofonsa.waitlist.signups";
    public static final String OPT_IN_METER = "abofonsa.waitlist.opt_in";

    private final Counter accepted;
    private final Counter duplicate;
    private final Counter rejected;
    private final Counter throttled;
    private final Counter mailFailed;

    private final Counter confirmed;
    private final Counter confirmInvalid;
    private final Counter unsubscribed;

    public WaitlistMetrics(MeterRegistry registry) {
        this.accepted = signups(registry, "accepted");
        this.duplicate = signups(registry, "duplicate");
        this.rejected = signups(registry, "rejected");
        this.throttled = signups(registry, "throttled");
        // Not a signup outcome — the row is saved either way — but the one failure that silently
        // strands somebody mid-funnel, since they can never complete an opt-in they never received.
        this.mailFailed = Counter.builder("abofonsa.waitlist.confirmation_mail_failures")
            .description("Confirmation emails that could not be sent. The signup is still captured.")
            .baseUnit("messages")
            .register(registry);

        this.confirmed = optIn(registry, "confirmed");
        this.confirmInvalid = optIn(registry, "invalid");
        this.unsubscribed = optIn(registry, "unsubscribed");
    }

    private static Counter signups(MeterRegistry registry, String outcome) {
        return Counter.builder(SIGNUPS_METER)
            .description("Waitlist submissions by outcome.")
            .baseUnit("submissions")
            .tag("outcome", outcome)
            .register(registry);
    }

    private static Counter optIn(MeterRegistry registry, String outcome) {
        return Counter.builder(OPT_IN_METER)
            .description("Double opt-in and unsubscribe outcomes.")
            .baseUnit("events")
            .tag("outcome", outcome)
            .register(registry);
    }

    public void signupAccepted() {
        accepted.increment();
    }

    public void signupDuplicate() {
        duplicate.increment();
    }

    /** Failed the honeypot or the dwell-time check. */
    public void signupRejected() {
        rejected.increment();
    }

    public void signupThrottled() {
        throttled.increment();
    }

    public void confirmationMailFailed() {
        mailFailed.increment();
    }

    public void optInConfirmed() {
        confirmed.increment();
    }

    public void optInInvalid() {
        confirmInvalid.increment();
    }

    public void optInUnsubscribed() {
        unsubscribed.increment();
    }
}
