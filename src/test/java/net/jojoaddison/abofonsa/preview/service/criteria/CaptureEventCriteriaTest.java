package net.jojoaddison.abofonsa.preview.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CaptureEventCriteriaTest {

    @Test
    void newCaptureEventCriteriaHasAllFiltersNullTest() {
        var captureEventCriteria = new CaptureEventCriteria();
        assertThat(captureEventCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void captureEventCriteriaFluentMethodsCreatesFiltersTest() {
        var captureEventCriteria = new CaptureEventCriteria();

        setAllFilters(captureEventCriteria);

        assertThat(captureEventCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void captureEventCriteriaCopyCreatesNullFilterTest() {
        var captureEventCriteria = new CaptureEventCriteria();
        var copy = captureEventCriteria.copy();

        assertThat(captureEventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(captureEventCriteria)
        );
    }

    @Test
    void captureEventCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var captureEventCriteria = new CaptureEventCriteria();
        setAllFilters(captureEventCriteria);

        var copy = captureEventCriteria.copy();

        assertThat(captureEventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(captureEventCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var captureEventCriteria = new CaptureEventCriteria();

        assertThat(captureEventCriteria).hasToString("CaptureEventCriteria{}");
    }

    private static void setAllFilters(CaptureEventCriteria captureEventCriteria) {
        captureEventCriteria.id();
        captureEventCriteria.eventType();
        captureEventCriteria.occurredAt();
        captureEventCriteria.occurredDate();
        captureEventCriteria.sessionHash();
        captureEventCriteria.locale();
        captureEventCriteria.sourcePage();
        captureEventCriteria.utmSource();
        captureEventCriteria.utmMedium();
        captureEventCriteria.utmCampaign();
        captureEventCriteria.referrerHost();
        captureEventCriteria.deviceType();
        captureEventCriteria.countryCode();
        captureEventCriteria.targetKey();
        captureEventCriteria.distinct();
    }

    private static Condition<CaptureEventCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEventType()) &&
                condition.apply(criteria.getOccurredAt()) &&
                condition.apply(criteria.getOccurredDate()) &&
                condition.apply(criteria.getSessionHash()) &&
                condition.apply(criteria.getLocale()) &&
                condition.apply(criteria.getSourcePage()) &&
                condition.apply(criteria.getUtmSource()) &&
                condition.apply(criteria.getUtmMedium()) &&
                condition.apply(criteria.getUtmCampaign()) &&
                condition.apply(criteria.getReferrerHost()) &&
                condition.apply(criteria.getDeviceType()) &&
                condition.apply(criteria.getCountryCode()) &&
                condition.apply(criteria.getTargetKey()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CaptureEventCriteria> copyFiltersAre(
        CaptureEventCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEventType(), copy.getEventType()) &&
                condition.apply(criteria.getOccurredAt(), copy.getOccurredAt()) &&
                condition.apply(criteria.getOccurredDate(), copy.getOccurredDate()) &&
                condition.apply(criteria.getSessionHash(), copy.getSessionHash()) &&
                condition.apply(criteria.getLocale(), copy.getLocale()) &&
                condition.apply(criteria.getSourcePage(), copy.getSourcePage()) &&
                condition.apply(criteria.getUtmSource(), copy.getUtmSource()) &&
                condition.apply(criteria.getUtmMedium(), copy.getUtmMedium()) &&
                condition.apply(criteria.getUtmCampaign(), copy.getUtmCampaign()) &&
                condition.apply(criteria.getReferrerHost(), copy.getReferrerHost()) &&
                condition.apply(criteria.getDeviceType(), copy.getDeviceType()) &&
                condition.apply(criteria.getCountryCode(), copy.getCountryCode()) &&
                condition.apply(criteria.getTargetKey(), copy.getTargetKey()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
