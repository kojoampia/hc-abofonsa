package net.jojoaddison.abofonsa.preview.service;

import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSignupDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.WaitlistSignupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.WaitlistSignup}.
 */
@Service
@Transactional
public class WaitlistSignupService {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistSignupService.class);

    private final WaitlistSignupRepository waitlistSignupRepository;

    private final WaitlistSignupMapper waitlistSignupMapper;

    public WaitlistSignupService(WaitlistSignupRepository waitlistSignupRepository, WaitlistSignupMapper waitlistSignupMapper) {
        this.waitlistSignupRepository = waitlistSignupRepository;
        this.waitlistSignupMapper = waitlistSignupMapper;
    }

    /**
     * Save a waitlistSignup.
     *
     * @param waitlistSignupDTO the entity to save.
     * @return the persisted entity.
     */
    public WaitlistSignupDTO save(WaitlistSignupDTO waitlistSignupDTO) {
        LOG.debug("Request to save WaitlistSignup : {}", waitlistSignupDTO);
        WaitlistSignup waitlistSignup = waitlistSignupMapper.toEntity(waitlistSignupDTO);
        waitlistSignup = waitlistSignupRepository.save(waitlistSignup);
        return waitlistSignupMapper.toDto(waitlistSignup);
    }

    /**
     * Update a waitlistSignup.
     *
     * <p>The DTO deliberately no longer carries {@code confirmationToken}, {@code unsubscribeToken},
     * {@code confirmationExpiresAt}, {@code ipHash} or {@code userAgent} — they are credentials and
     * personal data with no business being serialised to an admin screen. That leaves a trap:
     * {@code toEntity} maps a whole row from a DTO that no longer mentions those columns, so saving
     * the result would write nulls over them and silently break the opt-in and unsubscribe links of
     * whichever row was edited. They are carried across from the stored row instead.
     *
     * @param waitlistSignupDTO the entity to save.
     * @return the persisted entity.
     */
    public WaitlistSignupDTO update(WaitlistSignupDTO waitlistSignupDTO) {
        LOG.debug("Request to update WaitlistSignup : {}", waitlistSignupDTO);
        WaitlistSignup waitlistSignup = waitlistSignupMapper.toEntity(waitlistSignupDTO);
        waitlistSignupRepository.findById(waitlistSignupDTO.getId()).ifPresent(existing -> {
            waitlistSignup.setConfirmationToken(existing.getConfirmationToken());
            waitlistSignup.setConfirmationExpiresAt(existing.getConfirmationExpiresAt());
            waitlistSignup.setUnsubscribeToken(existing.getUnsubscribeToken());
            waitlistSignup.setIpHash(existing.getIpHash());
            waitlistSignup.setUserAgent(existing.getUserAgent());
        });
        WaitlistSignup saved = waitlistSignupRepository.save(waitlistSignup);
        return waitlistSignupMapper.toDto(saved);
    }

    /**
     * Partially update a waitlistSignup.
     *
     * @param waitlistSignupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WaitlistSignupDTO> partialUpdate(WaitlistSignupDTO waitlistSignupDTO) {
        LOG.debug("Request to partially update WaitlistSignup : {}", waitlistSignupDTO);

        return waitlistSignupRepository
            .findById(waitlistSignupDTO.getId())
            .map(existingWaitlistSignup -> {
                waitlistSignupMapper.partialUpdate(existingWaitlistSignup, waitlistSignupDTO);

                return existingWaitlistSignup;
            })
            .map(waitlistSignupRepository::save)
            .map(waitlistSignupMapper::toDto);
    }

    /**
     * Get one waitlistSignup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WaitlistSignupDTO> findOne(Long id) {
        LOG.debug("Request to get WaitlistSignup : {}", id);
        return waitlistSignupRepository.findById(id).map(waitlistSignupMapper::toDto);
    }

    /**
     * Delete the waitlistSignup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete WaitlistSignup : {}", id);
        waitlistSignupRepository.deleteById(id);
    }
}
