package net.jojoaddison.abofonsa.preview.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Keeps the rollup table current.
 *
 * <p>Two jobs, because they answer different questions. The frequent one keeps today's numbers
 * moving; the nightly one repairs anything the frequent one raced past — an event written a moment
 * after its bucket was rolled up would otherwise stay missing until something forced a rebuild.
 *
 * <p>Both are safe to run at any time and safe to run twice: {@link MetricRollupEngine#recompute}
 * deletes the window before rebuilding it, so a duplicate run produces the same rows rather than
 * double-counting.
 */
@Component
public class MetricRollupScheduler implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRollupScheduler.class);

    private final MetricRollupEngine engine;
    private final boolean backfillOnStartup;

    public MetricRollupScheduler(
        MetricRollupEngine engine,
        @Value("${abofonsa.metrics.backfill-on-startup:true}") boolean backfillOnStartup
    ) {
        this.engine = engine;
        this.backfillOnStartup = backfillOnStartup;
    }

    /** So a fresh deployment shows a populated dashboard rather than an empty chart. */
    @Override
    public void run(ApplicationArguments args) {
        if (!backfillOnStartup) {
            LOG.debug("Rollup backfill on startup is disabled");
            return;
        }
        try {
            engine.rebuildAll();
        } catch (RuntimeException e) {
            // A dashboard that is briefly stale is a much smaller problem than an application that
            // will not start. The scheduled jobs will fill it in shortly regardless.
            LOG.error("Rollup backfill failed on startup; the scheduled jobs will catch up", e);
        }
    }

    /**
     * Rolls up the recent past every ten minutes.
     *
     * <p>The window reaches two hours back rather than covering only the current bucket, so an
     * event that arrived just after its own bucket was computed is picked up on the next pass
     * instead of waiting for the nightly repair.
     */
    @Scheduled(fixedDelayString = "${abofonsa.metrics.incremental-interval-ms:600000}", initialDelay = 60_000)
    public void rollUpRecent() {
        Instant now = Instant.now();
        Instant to = now.plus(1, ChronoUnit.HOURS);
        try {
            for (BucketType bucketType : new BucketType[] { BucketType.HOUR, BucketType.DAY }) {
                Instant from = MetricRollupEngine.floor(now.minus(2, ChronoUnit.HOURS), bucketType);
                engine.recompute(bucketType, from, to);
            }
        } catch (RuntimeException e) {
            LOG.error("Incremental rollup failed", e);
        }
    }

    /** Nightly full rebuild — the backstop that makes every other pass forgiving. */
    @Scheduled(cron = "${abofonsa.metrics.reconcile-cron:0 20 2 * * *}")
    public void reconcile() {
        try {
            engine.rebuildAll();
        } catch (RuntimeException e) {
            LOG.error("Nightly rollup reconcile failed", e);
        }
    }
}
