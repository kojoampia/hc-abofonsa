package net.jojoaddison.abofonsa.preview.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LaunchMilestoneTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LaunchMilestone getLaunchMilestoneSample1() {
        return new LaunchMilestone().id(1L).phaseLabel("phaseLabel1").title("title1").displayOrder(1);
    }

    public static LaunchMilestone getLaunchMilestoneSample2() {
        return new LaunchMilestone().id(2L).phaseLabel("phaseLabel2").title("title2").displayOrder(2);
    }

    public static LaunchMilestone getLaunchMilestoneRandomSampleGenerator() {
        return new LaunchMilestone()
            .id(longCount.incrementAndGet())
            .phaseLabel(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
