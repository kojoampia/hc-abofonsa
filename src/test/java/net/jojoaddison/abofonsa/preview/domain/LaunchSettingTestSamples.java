package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LaunchSettingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LaunchSetting getLaunchSettingSample1() {
        return new LaunchSetting()
            .id(1L)
            .settingKey("settingKey1")
            .organisationName("organisationName1")
            .tagline("tagline1")
            .launchTimezone("launchTimezone1")
            .fundUrl("fundUrl1")
            .contactEmail("contactEmail1")
            .contactPhone("contactPhone1")
            .officeAddress("officeAddress1");
    }

    public static LaunchSetting getLaunchSettingSample2() {
        return new LaunchSetting()
            .id(2L)
            .settingKey("settingKey2")
            .organisationName("organisationName2")
            .tagline("tagline2")
            .launchTimezone("launchTimezone2")
            .fundUrl("fundUrl2")
            .contactEmail("contactEmail2")
            .contactPhone("contactPhone2")
            .officeAddress("officeAddress2");
    }

    public static LaunchSetting getLaunchSettingRandomSampleGenerator() {
        return new LaunchSetting()
            .id(longCount.incrementAndGet())
            .settingKey(UUID.randomUUID().toString())
            .organisationName(UUID.randomUUID().toString())
            .tagline(UUID.randomUUID().toString())
            .launchTimezone(UUID.randomUUID().toString())
            .fundUrl(UUID.randomUUID().toString())
            .contactEmail(UUID.randomUUID().toString())
            .contactPhone(UUID.randomUUID().toString())
            .officeAddress(UUID.randomUUID().toString());
    }
}
