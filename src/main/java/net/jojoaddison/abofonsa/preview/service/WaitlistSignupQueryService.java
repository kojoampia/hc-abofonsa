package net.jojoaddison.abofonsa.preview.service;

import net.jojoaddison.abofonsa.preview.domain.*; // for static metamodels
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.criteria.WaitlistSignupCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSignupDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.WaitlistSignupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WaitlistSignup} entities in the database.
 * The main input is a {@link WaitlistSignupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WaitlistSignupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WaitlistSignupQueryService extends QueryService<WaitlistSignup> {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistSignupQueryService.class);

    private final WaitlistSignupRepository waitlistSignupRepository;

    private final WaitlistSignupMapper waitlistSignupMapper;

    public WaitlistSignupQueryService(WaitlistSignupRepository waitlistSignupRepository, WaitlistSignupMapper waitlistSignupMapper) {
        this.waitlistSignupRepository = waitlistSignupRepository;
        this.waitlistSignupMapper = waitlistSignupMapper;
    }

    /**
     * Return a {@link Page} of {@link WaitlistSignupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WaitlistSignupDTO> findByCriteria(WaitlistSignupCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WaitlistSignup> specification = createSpecification(criteria);
        return waitlistSignupRepository.findAll(specification, page).map(waitlistSignupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WaitlistSignupCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WaitlistSignup> specification = createSpecification(criteria);
        return waitlistSignupRepository.count(specification);
    }

    /**
     * Function to convert {@link WaitlistSignupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WaitlistSignup> createSpecification(WaitlistSignupCriteria criteria) {
        Specification<WaitlistSignup> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), WaitlistSignup_.id),
                    buildStringSpecification(criteria.getEmail(), WaitlistSignup_.email),
                    buildStringSpecification(criteria.getEmailNormalized(), WaitlistSignup_.emailNormalized),
                    buildStringSpecification(criteria.getFullName(), WaitlistSignup_.fullName),
                    buildStringSpecification(criteria.getOrganisation(), WaitlistSignup_.organisation),
                    buildSpecification(criteria.getAudience(), WaitlistSignup_.audience),
                    buildSpecification(criteria.getPlanOfInterest(), WaitlistSignup_.planOfInterest),
                    buildSpecification(criteria.getStatus(), WaitlistSignup_.status),
                    buildStringSpecification(criteria.getLocale(), WaitlistSignup_.locale),
                    buildStringSpecification(criteria.getSourcePage(), WaitlistSignup_.sourcePage),
                    buildStringSpecification(criteria.getUtmSource(), WaitlistSignup_.utmSource),
                    buildStringSpecification(criteria.getUtmMedium(), WaitlistSignup_.utmMedium),
                    buildStringSpecification(criteria.getUtmCampaign(), WaitlistSignup_.utmCampaign),
                    buildStringSpecification(criteria.getReferrer(), WaitlistSignup_.referrer),
                    buildSpecification(criteria.getDeviceType(), WaitlistSignup_.deviceType),
                    buildSpecification(criteria.getConsentGiven(), WaitlistSignup_.consentGiven),
                    buildStringSpecification(criteria.getConfirmationToken(), WaitlistSignup_.confirmationToken),
                    buildRangeSpecification(criteria.getConfirmedAt(), WaitlistSignup_.confirmedAt),
                    buildRangeSpecification(criteria.getUnsubscribedAt(), WaitlistSignup_.unsubscribedAt),
                    buildRangeSpecification(criteria.getCapturedAt(), WaitlistSignup_.capturedAt),
                    buildStringSpecification(criteria.getIpHash(), WaitlistSignup_.ipHash),
                    buildStringSpecification(criteria.getUserAgent(), WaitlistSignup_.userAgent)
                )
            );
        }
        return specification;
    }
}
