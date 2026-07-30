package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CareServiceTeaserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CareServiceTeaser getCareServiceTeaserSample1() {
        return new CareServiceTeaser().id(1L).slug("slug1").name("name1").iconKey("iconKey1").availableOn("availableOn1").displayOrder(1);
    }

    public static CareServiceTeaser getCareServiceTeaserSample2() {
        return new CareServiceTeaser().id(2L).slug("slug2").name("name2").iconKey("iconKey2").availableOn("availableOn2").displayOrder(2);
    }

    public static CareServiceTeaser getCareServiceTeaserRandomSampleGenerator() {
        return new CareServiceTeaser()
            .id(longCount.incrementAndGet())
            .slug(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .iconKey(UUID.randomUUID().toString())
            .availableOn(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
