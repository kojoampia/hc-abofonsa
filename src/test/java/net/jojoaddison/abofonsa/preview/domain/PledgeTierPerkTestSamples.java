package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PledgeTierPerkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PledgeTierPerk getPledgeTierPerkSample1() {
        return new PledgeTierPerk().id(1L).label("label1").displayOrder(1);
    }

    public static PledgeTierPerk getPledgeTierPerkSample2() {
        return new PledgeTierPerk().id(2L).label("label2").displayOrder(2);
    }

    public static PledgeTierPerk getPledgeTierPerkRandomSampleGenerator() {
        return new PledgeTierPerk()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
