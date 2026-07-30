package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierPerkTestSamples.*;
import static net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PledgeTierPerkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PledgeTierPerk.class);
        PledgeTierPerk pledgeTierPerk1 = getPledgeTierPerkSample1();
        PledgeTierPerk pledgeTierPerk2 = new PledgeTierPerk();
        assertThat(pledgeTierPerk1).isNotEqualTo(pledgeTierPerk2);

        pledgeTierPerk2.setId(pledgeTierPerk1.getId());
        assertThat(pledgeTierPerk1).isEqualTo(pledgeTierPerk2);

        pledgeTierPerk2 = getPledgeTierPerkSample2();
        assertThat(pledgeTierPerk1).isNotEqualTo(pledgeTierPerk2);
    }

    @Test
    void tierTest() {
        PledgeTierPerk pledgeTierPerk = getPledgeTierPerkRandomSampleGenerator();
        PledgeTierTeaser pledgeTierTeaserBack = getPledgeTierTeaserRandomSampleGenerator();

        pledgeTierPerk.setTier(pledgeTierTeaserBack);
        assertThat(pledgeTierPerk.getTier()).isEqualTo(pledgeTierTeaserBack);

        pledgeTierPerk.tier(null);
        assertThat(pledgeTierPerk.getTier()).isNull();
    }
}
