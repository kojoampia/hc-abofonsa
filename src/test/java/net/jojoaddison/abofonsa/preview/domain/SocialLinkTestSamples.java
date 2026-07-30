package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SocialLinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SocialLink getSocialLinkSample1() {
        return new SocialLink().id(1L).label("label1").url("url1").iconKey("iconKey1").displayOrder(1);
    }

    public static SocialLink getSocialLinkSample2() {
        return new SocialLink().id(2L).label("label2").url("url2").iconKey("iconKey2").displayOrder(2);
    }

    public static SocialLink getSocialLinkRandomSampleGenerator() {
        return new SocialLink()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .iconKey(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
