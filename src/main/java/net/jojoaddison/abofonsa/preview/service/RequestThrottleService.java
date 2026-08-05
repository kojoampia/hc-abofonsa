package net.jojoaddison.abofonsa.preview.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * In-memory fixed-window rate limiting for the two endpoints that had none.
 *
 * <p><strong>Login.</strong> {@code POST /api/authenticate} accepted unlimited password attempts
 * against the one account that can read the waitlist — no lockout, no delay, and no log line either,
 * so a guessing campaign was invisible in the logs and in Prometheus alike. The endpoint that guards
 * the admin session was the only unprotected one; the waitlist form has been rate limited all along.
 *
 * <p><strong>The analytics beacon.</strong> {@code POST /api/public/events} is anonymous and writes
 * a row per call, so it was both a cheap way to grow {@code capture_event} without bound on a small
 * single-instance Postgres and a way to swamp the campaign-source split with invented traffic.
 *
 * <p>A <em>fixed</em> window rather than Caffeine's {@code expireAfterWrite}, which resets its clock
 * on every write: under that scheme a client hammering continuously would never let the entry expire
 * and a soft limit would silently become a permanent ban. The window index is part of the key, so
 * each window starts clean whatever the traffic.
 *
 * <p>In-memory, and therefore per-instance. That is honest for this deployment — one container, one
 * JVM — and if a second instance is ever added this becomes a limit of N times the configured value
 * rather than a broken one. The waitlist's own cap is counted in the database and is unaffected.
 */
@Service
public class RequestThrottleService {

    /** Failed logins per client before the endpoint stops trying, and for how long. */
    private static final int LOGIN_FAILURE_LIMIT = 10;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);

    private final Window loginFailures = new Window(LOGIN_WINDOW, 10_000);
    private final Window beaconEvents;

    public RequestThrottleService(@Value("${abofonsa.events.max-per-hour-per-client:600}") int beaconMaxPerHour) {
        this.beaconMaxPerHour = beaconMaxPerHour;
        this.beaconEvents = new Window(Duration.ofHours(1), 50_000);
    }

    private final int beaconMaxPerHour;

    /**
     * Whether this client may attempt a password right now.
     *
     * <p>Checked before authenticating rather than after, so that a locked-out client costs a map
     * lookup instead of a bcrypt verification — which is the expensive half, and the half an
     * attacker would otherwise be able to spend on our behalf.
     */
    public boolean loginAllowed(String clientKey) {
        return loginFailures.count(clientKey) < LOGIN_FAILURE_LIMIT;
    }

    /** Counts a failed password attempt. Returns the number of failures in the current window. */
    public int recordLoginFailure(String clientKey) {
        return loginFailures.increment(clientKey);
    }

    /**
     * Clears a client's failures after a successful login, so that someone who mistypes their
     * password nine times and then gets it right is not left one attempt from a lockout.
     */
    public void clearLoginFailures(String clientKey) {
        loginFailures.reset(clientKey);
    }

    /** Whether this client may report another analytics event. */
    public boolean beaconAllowed(String clientKey) {
        return beaconEvents.increment(clientKey) <= beaconMaxPerHour;
    }

    /** A counter per (client, window index) pair, discarded once its window is well past. */
    private static final class Window {

        private final Cache<String, AtomicInteger> counters;
        private final long seconds;

        private Window(Duration length, long maxEntries) {
            this.seconds = length.toSeconds();
            this.counters = Caffeine.newBuilder()
                // Twice the window: long enough that a live counter is never evicted early, short
                // enough that a finished one does not linger.
                .expireAfterWrite(length.multipliedBy(2))
                .maximumSize(maxEntries)
                .build();
        }

        private String key(String clientKey) {
            return clientKey + '|' + (Instant.now().getEpochSecond() / seconds);
        }

        private int increment(String clientKey) {
            return counters.get(key(clientKey), k -> new AtomicInteger()).incrementAndGet();
        }

        private int count(String clientKey) {
            AtomicInteger counter = counters.getIfPresent(key(clientKey));
            return counter == null ? 0 : counter.get();
        }

        private void reset(String clientKey) {
            counters.invalidate(key(clientKey));
        }
    }
}
