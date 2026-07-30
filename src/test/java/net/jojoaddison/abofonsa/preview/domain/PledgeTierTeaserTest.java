package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierPerkTestSamples.*;
import static net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PledgeTierTeaserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PledgeTierTeaser.class);
        PledgeTierTeaser pledgeTierTeaser1 = getPledgeTierTeaserSample1();
        PledgeTierTeaser pledgeTierTeaser2 = new PledgeTierTeaser();
        assertThat(pledgeTierTeaser1).isNotEqualTo(pledgeTierTeaser2);

        pledgeTierTeaser2.setId(pledgeTierTeaser1.getId());
        assertThat(pledgeTierTeaser1).isEqualTo(pledgeTierTeaser2);

        pledgeTierTeaser2 = getPledgeTierTeaserSample2();
        assertThat(pledgeTierTeaser1).isNotEqualTo(pledgeTierTeaser2);
    }

    @Test
    void perkTest() {
        PledgeTierTeaser pledgeTierTeaser = getPledgeTierTeaserRandomSampleGenerator();
        PledgeTierPerk pledgeTierPerkBack = getPledgeTierPerkRandomSampleGenerator();

        pledgeTierTeaser.addPerk(pledgeTierPerkBack);
        assertThat(pledgeTierTeaser.getPerks()).containsOnly(pledgeTierPerkBack);
        assertThat(pledgeTierPerkBack.getTier()).isEqualTo(pledgeTierTeaser);

        pledgeTierTeaser.removePerk(pledgeTierPerkBack);
        assertThat(pledgeTierTeaser.getPerks()).doesNotContain(pledgeTierPerkBack);
        assertThat(pledgeTierPerkBack.getTier()).isNull();

        pledgeTierTeaser.perks(new HashSet<>(Set.of(pledgeTierPerkBack)));
        assertThat(pledgeTierTeaser.getPerks()).containsOnly(pledgeTierPerkBack);
        assertThat(pledgeTierPerkBack.getTier()).isEqualTo(pledgeTierTeaser);

        pledgeTierTeaser.setPerks(new HashSet<>());
        assertThat(pledgeTierTeaser.getPerks()).doesNotContain(pledgeTierPerkBack);
        assertThat(pledgeTierPerkBack.getTier()).isNull();
    }
}
