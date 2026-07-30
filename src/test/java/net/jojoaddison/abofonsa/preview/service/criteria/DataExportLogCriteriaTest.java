package net.jojoaddison.abofonsa.preview.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DataExportLogCriteriaTest {

    @Test
    void newDataExportLogCriteriaHasAllFiltersNullTest() {
        var dataExportLogCriteria = new DataExportLogCriteria();
        assertThat(dataExportLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void dataExportLogCriteriaFluentMethodsCreatesFiltersTest() {
        var dataExportLogCriteria = new DataExportLogCriteria();

        setAllFilters(dataExportLogCriteria);

        assertThat(dataExportLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void dataExportLogCriteriaCopyCreatesNullFilterTest() {
        var dataExportLogCriteria = new DataExportLogCriteria();
        var copy = dataExportLogCriteria.copy();

        assertThat(dataExportLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(dataExportLogCriteria)
        );
    }

    @Test
    void dataExportLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var dataExportLogCriteria = new DataExportLogCriteria();
        setAllFilters(dataExportLogCriteria);

        var copy = dataExportLogCriteria.copy();

        assertThat(dataExportLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(dataExportLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var dataExportLogCriteria = new DataExportLogCriteria();

        assertThat(dataExportLogCriteria).hasToString("DataExportLogCriteria{}");
    }

    private static void setAllFilters(DataExportLogCriteria dataExportLogCriteria) {
        dataExportLogCriteria.id();
        dataExportLogCriteria.exportKind();
        dataExportLogCriteria.format();
        dataExportLogCriteria.rangeFrom();
        dataExportLogCriteria.rangeTo();
        dataExportLogCriteria.bucketType();
        dataExportLogCriteria.filterSummary();
        dataExportLogCriteria.rowCount();
        dataExportLogCriteria.requestedBy();
        dataExportLogCriteria.requestedAt();
        dataExportLogCriteria.durationMs();
        dataExportLogCriteria.distinct();
    }

    private static Condition<DataExportLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getExportKind()) &&
                condition.apply(criteria.getFormat()) &&
                condition.apply(criteria.getRangeFrom()) &&
                condition.apply(criteria.getRangeTo()) &&
                condition.apply(criteria.getBucketType()) &&
                condition.apply(criteria.getFilterSummary()) &&
                condition.apply(criteria.getRowCount()) &&
                condition.apply(criteria.getRequestedBy()) &&
                condition.apply(criteria.getRequestedAt()) &&
                condition.apply(criteria.getDurationMs()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DataExportLogCriteria> copyFiltersAre(
        DataExportLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getExportKind(), copy.getExportKind()) &&
                condition.apply(criteria.getFormat(), copy.getFormat()) &&
                condition.apply(criteria.getRangeFrom(), copy.getRangeFrom()) &&
                condition.apply(criteria.getRangeTo(), copy.getRangeTo()) &&
                condition.apply(criteria.getBucketType(), copy.getBucketType()) &&
                condition.apply(criteria.getFilterSummary(), copy.getFilterSummary()) &&
                condition.apply(criteria.getRowCount(), copy.getRowCount()) &&
                condition.apply(criteria.getRequestedBy(), copy.getRequestedBy()) &&
                condition.apply(criteria.getRequestedAt(), copy.getRequestedAt()) &&
                condition.apply(criteria.getDurationMs(), copy.getDurationMs()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
