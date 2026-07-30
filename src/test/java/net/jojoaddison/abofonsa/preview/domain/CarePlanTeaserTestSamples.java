package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CarePlanTeaserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CarePlanTeaser getCarePlanTeaserSample1() {
        return new CarePlanTeaser()
            .id(1L)
            .name("name1")
            .priceCurrency("priceCurrency1")
            .pricePeriod("pricePeriod1")
            .priceNote("priceNote1")
            .displayOrder(1);
    }

    public static CarePlanTeaser getCarePlanTeaserSample2() {
        return new CarePlanTeaser()
            .id(2L)
            .name("name2")
            .priceCurrency("priceCurrency2")
            .pricePeriod("pricePeriod2")
            .priceNote("priceNote2")
            .displayOrder(2);
    }

    public static CarePlanTeaser getCarePlanTeaserRandomSampleGenerator() {
        return new CarePlanTeaser()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .priceCurrency(UUID.randomUUID().toString())
            .pricePeriod(UUID.randomUUID().toString())
            .priceNote(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
