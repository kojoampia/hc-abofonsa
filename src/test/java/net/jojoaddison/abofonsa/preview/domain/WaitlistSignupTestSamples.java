package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WaitlistSignupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static WaitlistSignup getWaitlistSignupSample1() {
        return new WaitlistSignup()
            .id(1L)
            .email("email1")
            .emailNormalized("emailNormalized1")
            .fullName("fullName1")
            .organisation("organisation1")
            .locale("locale1")
            .sourcePage("sourcePage1")
            .utmSource("utmSource1")
            .utmMedium("utmMedium1")
            .utmCampaign("utmCampaign1")
            .referrer("referrer1")
            .confirmationToken("confirmationToken1")
            .ipHash("ipHash1")
            .userAgent("userAgent1");
    }

    public static WaitlistSignup getWaitlistSignupSample2() {
        return new WaitlistSignup()
            .id(2L)
            .email("email2")
            .emailNormalized("emailNormalized2")
            .fullName("fullName2")
            .organisation("organisation2")
            .locale("locale2")
            .sourcePage("sourcePage2")
            .utmSource("utmSource2")
            .utmMedium("utmMedium2")
            .utmCampaign("utmCampaign2")
            .referrer("referrer2")
            .confirmationToken("confirmationToken2")
            .ipHash("ipHash2")
            .userAgent("userAgent2");
    }

    public static WaitlistSignup getWaitlistSignupRandomSampleGenerator() {
        return new WaitlistSignup()
            .id(longCount.incrementAndGet())
            .email(UUID.randomUUID().toString())
            .emailNormalized(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .organisation(UUID.randomUUID().toString())
            .locale(UUID.randomUUID().toString())
            .sourcePage(UUID.randomUUID().toString())
            .utmSource(UUID.randomUUID().toString())
            .utmMedium(UUID.randomUUID().toString())
            .utmCampaign(UUID.randomUUID().toString())
            .referrer(UUID.randomUUID().toString())
            .confirmationToken(UUID.randomUUID().toString())
            .ipHash(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString());
    }
}
