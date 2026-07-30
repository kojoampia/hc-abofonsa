package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DataExportLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DataExportLog getDataExportLogSample1() {
        return new DataExportLog().id(1L).filterSummary("filterSummary1").rowCount(1).requestedBy("requestedBy1").durationMs(1L);
    }

    public static DataExportLog getDataExportLogSample2() {
        return new DataExportLog().id(2L).filterSummary("filterSummary2").rowCount(2).requestedBy("requestedBy2").durationMs(2L);
    }

    public static DataExportLog getDataExportLogRandomSampleGenerator() {
        return new DataExportLog()
            .id(longCount.incrementAndGet())
            .filterSummary(UUID.randomUUID().toString())
            .rowCount(intCount.incrementAndGet())
            .requestedBy(UUID.randomUUID().toString())
            .durationMs(longCount.incrementAndGet());
    }
}
