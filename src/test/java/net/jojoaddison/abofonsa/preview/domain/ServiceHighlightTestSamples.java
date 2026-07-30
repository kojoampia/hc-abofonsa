package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceHighlightTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ServiceHighlight getServiceHighlightSample1() {
        return new ServiceHighlight().id(1L).label("label1").displayOrder(1);
    }

    public static ServiceHighlight getServiceHighlightSample2() {
        return new ServiceHighlight().id(2L).label("label2").displayOrder(2);
    }

    public static ServiceHighlight getServiceHighlightRandomSampleGenerator() {
        return new ServiceHighlight()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
