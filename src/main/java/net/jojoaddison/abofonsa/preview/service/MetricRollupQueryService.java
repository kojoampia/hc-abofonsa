package net.jojoaddison.abofonsa.preview.service;

import net.jojoaddison.abofonsa.preview.domain.*; // for static metamodels
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.service.criteria.MetricRollupCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.MetricRollupDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.MetricRollupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MetricRollup} entities in the database.
 * The main input is a {@link MetricRollupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MetricRollupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MetricRollupQueryService extends QueryService<MetricRollup> {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRollupQueryService.class);

    private final MetricRollupRepository metricRollupRepository;

    private final MetricRollupMapper metricRollupMapper;

    public MetricRollupQueryService(MetricRollupRepository metricRollupRepository, MetricRollupMapper metricRollupMapper) {
        this.metricRollupRepository = metricRollupRepository;
        this.metricRollupMapper = metricRollupMapper;
    }

    /**
     * Return a {@link Page} of {@link MetricRollupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MetricRollupDTO> findByCriteria(MetricRollupCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MetricRollup> specification = createSpecification(criteria);
        return metricRollupRepository.findAll(specification, page).map(metricRollupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MetricRollupCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MetricRollup> specification = createSpecification(criteria);
        return metricRollupRepository.count(specification);
    }

    /**
     * Function to convert {@link MetricRollupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MetricRollup> createSpecification(MetricRollupCriteria criteria) {
        Specification<MetricRollup> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), MetricRollup_.id),
                    buildSpecification(criteria.getMetricKey(), MetricRollup_.metricKey),
                    buildSpecification(criteria.getBucketType(), MetricRollup_.bucketType),
                    buildRangeSpecification(criteria.getBucketStart(), MetricRollup_.bucketStart),
                    buildRangeSpecification(criteria.getBucketEnd(), MetricRollup_.bucketEnd),
                    buildStringSpecification(criteria.getDimensionName(), MetricRollup_.dimensionName),
                    buildStringSpecification(criteria.getDimensionValue(), MetricRollup_.dimensionValue),
                    buildRangeSpecification(criteria.getValue(), MetricRollup_.value),
                    buildRangeSpecification(criteria.getComputedAt(), MetricRollup_.computedAt)
                )
            );
        }
        return specification;
    }
}
