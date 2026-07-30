package net.jojoaddison.abofonsa.preview.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MetricRollupCriteriaTest {

    @Test
    void newMetricRollupCriteriaHasAllFiltersNullTest() {
        var metricRollupCriteria = new MetricRollupCriteria();
        assertThat(metricRollupCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void metricRollupCriteriaFluentMethodsCreatesFiltersTest() {
        var metricRollupCriteria = new MetricRollupCriteria();

        setAllFilters(metricRollupCriteria);

        assertThat(metricRollupCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void metricRollupCriteriaCopyCreatesNullFilterTest() {
        var metricRollupCriteria = new MetricRollupCriteria();
        var copy = metricRollupCriteria.copy();

        assertThat(metricRollupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(metricRollupCriteria)
        );
    }

    @Test
    void metricRollupCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var metricRollupCriteria = new MetricRollupCriteria();
        setAllFilters(metricRollupCriteria);

        var copy = metricRollupCriteria.copy();

        assertThat(metricRollupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(metricRollupCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var metricRollupCriteria = new MetricRollupCriteria();

        assertThat(metricRollupCriteria).hasToString("MetricRollupCriteria{}");
    }

    private static void setAllFilters(MetricRollupCriteria metricRollupCriteria) {
        metricRollupCriteria.id();
        metricRollupCriteria.metricKey();
        metricRollupCriteria.bucketType();
        metricRollupCriteria.bucketStart();
        metricRollupCriteria.bucketEnd();
        metricRollupCriteria.dimensionName();
        metricRollupCriteria.dimensionValue();
        metricRollupCriteria.value();
        metricRollupCriteria.computedAt();
        metricRollupCriteria.distinct();
    }

    private static Condition<MetricRollupCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMetricKey()) &&
                condition.apply(criteria.getBucketType()) &&
                condition.apply(criteria.getBucketStart()) &&
                condition.apply(criteria.getBucketEnd()) &&
                condition.apply(criteria.getDimensionName()) &&
                condition.apply(criteria.getDimensionValue()) &&
                condition.apply(criteria.getValue()) &&
                condition.apply(criteria.getComputedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MetricRollupCriteria> copyFiltersAre(
        MetricRollupCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMetricKey(), copy.getMetricKey()) &&
                condition.apply(criteria.getBucketType(), copy.getBucketType()) &&
                condition.apply(criteria.getBucketStart(), copy.getBucketStart()) &&
                condition.apply(criteria.getBucketEnd(), copy.getBucketEnd()) &&
                condition.apply(criteria.getDimensionName(), copy.getDimensionName()) &&
                condition.apply(criteria.getDimensionValue(), copy.getDimensionValue()) &&
                condition.apply(criteria.getValue(), copy.getValue()) &&
                condition.apply(criteria.getComputedAt(), copy.getComputedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
