package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PledgeTierTeaserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PledgeTierTeaser getPledgeTierTeaserSample1() {
        return new PledgeTierTeaser().id(1L).name("name1").currency("currency1").handoffUrl("handoffUrl1").displayOrder(1);
    }

    public static PledgeTierTeaser getPledgeTierTeaserSample2() {
        return new PledgeTierTeaser().id(2L).name("name2").currency("currency2").handoffUrl("handoffUrl2").displayOrder(2);
    }

    public static PledgeTierTeaser getPledgeTierTeaserRandomSampleGenerator() {
        return new PledgeTierTeaser()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .handoffUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
