package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CaptureEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CaptureEvent getCaptureEventSample1() {
        return new CaptureEvent()
            .id(1L)
            .sessionHash("sessionHash1")
            .locale("locale1")
            .sourcePage("sourcePage1")
            .utmSource("utmSource1")
            .utmMedium("utmMedium1")
            .utmCampaign("utmCampaign1")
            .referrerHost("referrerHost1")
            .countryCode("countryCode1")
            .targetKey("targetKey1");
    }

    public static CaptureEvent getCaptureEventSample2() {
        return new CaptureEvent()
            .id(2L)
            .sessionHash("sessionHash2")
            .locale("locale2")
            .sourcePage("sourcePage2")
            .utmSource("utmSource2")
            .utmMedium("utmMedium2")
            .utmCampaign("utmCampaign2")
            .referrerHost("referrerHost2")
            .countryCode("countryCode2")
            .targetKey("targetKey2");
    }

    public static CaptureEvent getCaptureEventRandomSampleGenerator() {
        return new CaptureEvent()
            .id(longCount.incrementAndGet())
            .sessionHash(UUID.randomUUID().toString())
            .locale(UUID.randomUUID().toString())
            .sourcePage(UUID.randomUUID().toString())
            .utmSource(UUID.randomUUID().toString())
            .utmMedium(UUID.randomUUID().toString())
            .utmCampaign(UUID.randomUUID().toString())
            .referrerHost(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString())
            .targetKey(UUID.randomUUID().toString());
    }
}
