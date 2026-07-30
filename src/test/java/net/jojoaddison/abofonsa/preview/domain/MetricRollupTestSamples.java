package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricRollupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MetricRollup getMetricRollupSample1() {
        return new MetricRollup().id(1L).dimensionName("dimensionName1").dimensionValue("dimensionValue1").value(1L);
    }

    public static MetricRollup getMetricRollupSample2() {
        return new MetricRollup().id(2L).dimensionName("dimensionName2").dimensionValue("dimensionValue2").value(2L);
    }

    public static MetricRollup getMetricRollupRandomSampleGenerator() {
        return new MetricRollup()
            .id(longCount.incrementAndGet())
            .dimensionName(UUID.randomUUID().toString())
            .dimensionValue(UUID.randomUUID().toString())
            .value(longCount.incrementAndGet());
    }
}
