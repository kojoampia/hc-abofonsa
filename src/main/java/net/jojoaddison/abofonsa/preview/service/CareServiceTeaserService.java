package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import net.jojoaddison.abofonsa.preview.repository.CareServiceTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CareServiceTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CareServiceTeaserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser}.
 */
@Service
@Transactional
public class CareServiceTeaserService {

    private static final Logger LOG = LoggerFactory.getLogger(CareServiceTeaserService.class);

    private final CareServiceTeaserRepository careServiceTeaserRepository;

    private final CareServiceTeaserMapper careServiceTeaserMapper;

    public CareServiceTeaserService(
        CareServiceTeaserRepository careServiceTeaserRepository,
        CareServiceTeaserMapper careServiceTeaserMapper
    ) {
        this.careServiceTeaserRepository = careServiceTeaserRepository;
        this.careServiceTeaserMapper = careServiceTeaserMapper;
    }

    /**
     * Save a careServiceTeaser.
     *
     * @param careServiceTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public CareServiceTeaserDTO save(CareServiceTeaserDTO careServiceTeaserDTO) {
        LOG.debug("Request to save CareServiceTeaser : {}", careServiceTeaserDTO);
        CareServiceTeaser careServiceTeaser = careServiceTeaserMapper.toEntity(careServiceTeaserDTO);
        careServiceTeaser = careServiceTeaserRepository.save(careServiceTeaser);
        return careServiceTeaserMapper.toDto(careServiceTeaser);
    }

    /**
     * Update a careServiceTeaser.
     *
     * @param careServiceTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public CareServiceTeaserDTO update(CareServiceTeaserDTO careServiceTeaserDTO) {
        LOG.debug("Request to update CareServiceTeaser : {}", careServiceTeaserDTO);
        CareServiceTeaser careServiceTeaser = careServiceTeaserMapper.toEntity(careServiceTeaserDTO);
        careServiceTeaser = careServiceTeaserRepository.save(careServiceTeaser);
        return careServiceTeaserMapper.toDto(careServiceTeaser);
    }

    /**
     * Partially update a careServiceTeaser.
     *
     * @param careServiceTeaserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CareServiceTeaserDTO> partialUpdate(CareServiceTeaserDTO careServiceTeaserDTO) {
        LOG.debug("Request to partially update CareServiceTeaser : {}", careServiceTeaserDTO);

        return careServiceTeaserRepository
            .findById(careServiceTeaserDTO.getId())
            .map(existingCareServiceTeaser -> {
                careServiceTeaserMapper.partialUpdate(existingCareServiceTeaser, careServiceTeaserDTO);

                return existingCareServiceTeaser;
            })
            .map(careServiceTeaserRepository::save)
            .map(careServiceTeaserMapper::toDto);
    }

    /**
     * Get all the careServiceTeasers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CareServiceTeaserDTO> findAll() {
        LOG.debug("Request to get all CareServiceTeasers");
        return careServiceTeaserRepository
            .findAll()
            .stream()
            .map(careServiceTeaserMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one careServiceTeaser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CareServiceTeaserDTO> findOne(Long id) {
        LOG.debug("Request to get CareServiceTeaser : {}", id);
        return careServiceTeaserRepository.findById(id).map(careServiceTeaserMapper::toDto);
    }

    /**
     * Delete the careServiceTeaser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CareServiceTeaser : {}", id);
        careServiceTeaserRepository.deleteById(id);
    }
}
