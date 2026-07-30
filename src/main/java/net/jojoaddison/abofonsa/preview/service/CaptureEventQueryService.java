package net.jojoaddison.abofonsa.preview.service;

import net.jojoaddison.abofonsa.preview.domain.*; // for static metamodels
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.service.criteria.CaptureEventCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CaptureEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CaptureEvent} entities in the database.
 * The main input is a {@link CaptureEventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CaptureEventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CaptureEventQueryService extends QueryService<CaptureEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureEventQueryService.class);

    private final CaptureEventRepository captureEventRepository;

    private final CaptureEventMapper captureEventMapper;

    public CaptureEventQueryService(CaptureEventRepository captureEventRepository, CaptureEventMapper captureEventMapper) {
        this.captureEventRepository = captureEventRepository;
        this.captureEventMapper = captureEventMapper;
    }

    /**
     * Return a {@link Page} of {@link CaptureEventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CaptureEventDTO> findByCriteria(CaptureEventCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CaptureEvent> specification = createSpecification(criteria);
        return captureEventRepository.findAll(specification, page).map(captureEventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CaptureEventCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CaptureEvent> specification = createSpecification(criteria);
        return captureEventRepository.count(specification);
    }

    /**
     * Function to convert {@link CaptureEventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CaptureEvent> createSpecification(CaptureEventCriteria criteria) {
        Specification<CaptureEvent> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CaptureEvent_.id),
                    buildSpecification(criteria.getEventType(), CaptureEvent_.eventType),
                    buildRangeSpecification(criteria.getOccurredAt(), CaptureEvent_.occurredAt),
                    buildRangeSpecification(criteria.getOccurredDate(), CaptureEvent_.occurredDate),
                    buildStringSpecification(criteria.getSessionHash(), CaptureEvent_.sessionHash),
                    buildStringSpecification(criteria.getLocale(), CaptureEvent_.locale),
                    buildStringSpecification(criteria.getSourcePage(), CaptureEvent_.sourcePage),
                    buildStringSpecification(criteria.getUtmSource(), CaptureEvent_.utmSource),
                    buildStringSpecification(criteria.getUtmMedium(), CaptureEvent_.utmMedium),
                    buildStringSpecification(criteria.getUtmCampaign(), CaptureEvent_.utmCampaign),
                    buildStringSpecification(criteria.getReferrerHost(), CaptureEvent_.referrerHost),
                    buildSpecification(criteria.getDeviceType(), CaptureEvent_.deviceType),
                    buildStringSpecification(criteria.getCountryCode(), CaptureEvent_.countryCode),
                    buildStringSpecification(criteria.getTargetKey(), CaptureEvent_.targetKey)
                )
            );
        }
        return specification;
    }
}
