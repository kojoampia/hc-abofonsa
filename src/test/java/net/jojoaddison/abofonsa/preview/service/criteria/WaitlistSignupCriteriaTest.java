package net.jojoaddison.abofonsa.preview.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WaitlistSignupCriteriaTest {

    @Test
    void newWaitlistSignupCriteriaHasAllFiltersNullTest() {
        var waitlistSignupCriteria = new WaitlistSignupCriteria();
        assertThat(waitlistSignupCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void waitlistSignupCriteriaFluentMethodsCreatesFiltersTest() {
        var waitlistSignupCriteria = new WaitlistSignupCriteria();

        setAllFilters(waitlistSignupCriteria);

        assertThat(waitlistSignupCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void waitlistSignupCriteriaCopyCreatesNullFilterTest() {
        var waitlistSignupCriteria = new WaitlistSignupCriteria();
        var copy = waitlistSignupCriteria.copy();

        assertThat(waitlistSignupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(waitlistSignupCriteria)
        );
    }

    @Test
    void waitlistSignupCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var waitlistSignupCriteria = new WaitlistSignupCriteria();
        setAllFilters(waitlistSignupCriteria);

        var copy = waitlistSignupCriteria.copy();

        assertThat(waitlistSignupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(waitlistSignupCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var waitlistSignupCriteria = new WaitlistSignupCriteria();

        assertThat(waitlistSignupCriteria).hasToString("WaitlistSignupCriteria{}");
    }

    private static void setAllFilters(WaitlistSignupCriteria waitlistSignupCriteria) {
        waitlistSignupCriteria.id();
        waitlistSignupCriteria.email();
        waitlistSignupCriteria.emailNormalized();
        waitlistSignupCriteria.fullName();
        waitlistSignupCriteria.organisation();
        waitlistSignupCriteria.audience();
        waitlistSignupCriteria.planOfInterest();
        waitlistSignupCriteria.status();
        waitlistSignupCriteria.locale();
        waitlistSignupCriteria.sourcePage();
        waitlistSignupCriteria.utmSource();
        waitlistSignupCriteria.utmMedium();
        waitlistSignupCriteria.utmCampaign();
        waitlistSignupCriteria.referrer();
        waitlistSignupCriteria.deviceType();
        waitlistSignupCriteria.consentGiven();
        waitlistSignupCriteria.confirmedAt();
        waitlistSignupCriteria.unsubscribedAt();
        waitlistSignupCriteria.capturedAt();
        waitlistSignupCriteria.distinct();
    }

    private static Condition<WaitlistSignupCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getEmailNormalized()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getOrganisation()) &&
                condition.apply(criteria.getAudience()) &&
                condition.apply(criteria.getPlanOfInterest()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getLocale()) &&
                condition.apply(criteria.getSourcePage()) &&
                condition.apply(criteria.getUtmSource()) &&
                condition.apply(criteria.getUtmMedium()) &&
                condition.apply(criteria.getUtmCampaign()) &&
                condition.apply(criteria.getReferrer()) &&
                condition.apply(criteria.getDeviceType()) &&
                condition.apply(criteria.getConsentGiven()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getUnsubscribedAt()) &&
                condition.apply(criteria.getCapturedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WaitlistSignupCriteria> copyFiltersAre(
        WaitlistSignupCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getEmailNormalized(), copy.getEmailNormalized()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getOrganisation(), copy.getOrganisation()) &&
                condition.apply(criteria.getAudience(), copy.getAudience()) &&
                condition.apply(criteria.getPlanOfInterest(), copy.getPlanOfInterest()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getLocale(), copy.getLocale()) &&
                condition.apply(criteria.getSourcePage(), copy.getSourcePage()) &&
                condition.apply(criteria.getUtmSource(), copy.getUtmSource()) &&
                condition.apply(criteria.getUtmMedium(), copy.getUtmMedium()) &&
                condition.apply(criteria.getUtmCampaign(), copy.getUtmCampaign()) &&
                condition.apply(criteria.getReferrer(), copy.getReferrer()) &&
                condition.apply(criteria.getDeviceType(), copy.getDeviceType()) &&
                condition.apply(criteria.getConsentGiven(), copy.getConsentGiven()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getUnsubscribedAt(), copy.getUnsubscribedAt()) &&
                condition.apply(criteria.getCapturedAt(), copy.getCapturedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
