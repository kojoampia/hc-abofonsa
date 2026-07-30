package net.jojoaddison.abofonsa.preview.service;

import net.jojoaddison.abofonsa.preview.domain.*; // for static metamodels
import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import net.jojoaddison.abofonsa.preview.repository.DataExportLogRepository;
import net.jojoaddison.abofonsa.preview.service.criteria.DataExportLogCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.DataExportLogDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.DataExportLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DataExportLog} entities in the database.
 * The main input is a {@link DataExportLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DataExportLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DataExportLogQueryService extends QueryService<DataExportLog> {

    private static final Logger LOG = LoggerFactory.getLogger(DataExportLogQueryService.class);

    private final DataExportLogRepository dataExportLogRepository;

    private final DataExportLogMapper dataExportLogMapper;

    public DataExportLogQueryService(DataExportLogRepository dataExportLogRepository, DataExportLogMapper dataExportLogMapper) {
        this.dataExportLogRepository = dataExportLogRepository;
        this.dataExportLogMapper = dataExportLogMapper;
    }

    /**
     * Return a {@link Page} of {@link DataExportLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DataExportLogDTO> findByCriteria(DataExportLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DataExportLog> specification = createSpecification(criteria);
        return dataExportLogRepository.findAll(specification, page).map(dataExportLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DataExportLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DataExportLog> specification = createSpecification(criteria);
        return dataExportLogRepository.count(specification);
    }

    /**
     * Function to convert {@link DataExportLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DataExportLog> createSpecification(DataExportLogCriteria criteria) {
        Specification<DataExportLog> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DataExportLog_.id),
                    buildSpecification(criteria.getExportKind(), DataExportLog_.exportKind),
                    buildSpecification(criteria.getFormat(), DataExportLog_.format),
                    buildRangeSpecification(criteria.getRangeFrom(), DataExportLog_.rangeFrom),
                    buildRangeSpecification(criteria.getRangeTo(), DataExportLog_.rangeTo),
                    buildSpecification(criteria.getBucketType(), DataExportLog_.bucketType),
                    buildStringSpecification(criteria.getFilterSummary(), DataExportLog_.filterSummary),
                    buildRangeSpecification(criteria.getRowCount(), DataExportLog_.rowCount),
                    buildStringSpecification(criteria.getRequestedBy(), DataExportLog_.requestedBy),
                    buildRangeSpecification(criteria.getRequestedAt(), DataExportLog_.requestedAt),
                    buildRangeSpecification(criteria.getDurationMs(), DataExportLog_.durationMs)
                )
            );
        }
        return specification;
    }
}
