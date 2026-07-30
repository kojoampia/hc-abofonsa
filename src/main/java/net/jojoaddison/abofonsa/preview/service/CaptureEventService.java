package net.jojoaddison.abofonsa.preview.service;

import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CaptureEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.CaptureEvent}.
 */
@Service
@Transactional
public class CaptureEventService {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureEventService.class);

    private final CaptureEventRepository captureEventRepository;

    private final CaptureEventMapper captureEventMapper;

    public CaptureEventService(CaptureEventRepository captureEventRepository, CaptureEventMapper captureEventMapper) {
        this.captureEventRepository = captureEventRepository;
        this.captureEventMapper = captureEventMapper;
    }

    /**
     * Save a captureEvent.
     *
     * @param captureEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CaptureEventDTO save(CaptureEventDTO captureEventDTO) {
        LOG.debug("Request to save CaptureEvent : {}", captureEventDTO);
        CaptureEvent captureEvent = captureEventMapper.toEntity(captureEventDTO);
        captureEvent = captureEventRepository.save(captureEvent);
        return captureEventMapper.toDto(captureEvent);
    }

    /**
     * Update a captureEvent.
     *
     * @param captureEventDTO the entity to save.
     * @return the persisted entity.
     */
    public CaptureEventDTO update(CaptureEventDTO captureEventDTO) {
        LOG.debug("Request to update CaptureEvent : {}", captureEventDTO);
        CaptureEvent captureEvent = captureEventMapper.toEntity(captureEventDTO);
        captureEvent = captureEventRepository.save(captureEvent);
        return captureEventMapper.toDto(captureEvent);
    }

    /**
     * Partially update a captureEvent.
     *
     * @param captureEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CaptureEventDTO> partialUpdate(CaptureEventDTO captureEventDTO) {
        LOG.debug("Request to partially update CaptureEvent : {}", captureEventDTO);

        return captureEventRepository
            .findById(captureEventDTO.getId())
            .map(existingCaptureEvent -> {
                captureEventMapper.partialUpdate(existingCaptureEvent, captureEventDTO);

                return existingCaptureEvent;
            })
            .map(captureEventRepository::save)
            .map(captureEventMapper::toDto);
    }

    /**
     * Get one captureEvent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CaptureEventDTO> findOne(Long id) {
        LOG.debug("Request to get CaptureEvent : {}", id);
        return captureEventRepository.findById(id).map(captureEventMapper::toDto);
    }

    /**
     * Delete the captureEvent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CaptureEvent : {}", id);
        captureEventRepository.deleteById(id);
    }
}
