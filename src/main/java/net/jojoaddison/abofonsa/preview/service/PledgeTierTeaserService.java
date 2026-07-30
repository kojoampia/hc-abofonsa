package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.PledgeTierTeaserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser}.
 */
@Service
@Transactional
public class PledgeTierTeaserService {

    private static final Logger LOG = LoggerFactory.getLogger(PledgeTierTeaserService.class);

    private final PledgeTierTeaserRepository pledgeTierTeaserRepository;

    private final PledgeTierTeaserMapper pledgeTierTeaserMapper;

    public PledgeTierTeaserService(PledgeTierTeaserRepository pledgeTierTeaserRepository, PledgeTierTeaserMapper pledgeTierTeaserMapper) {
        this.pledgeTierTeaserRepository = pledgeTierTeaserRepository;
        this.pledgeTierTeaserMapper = pledgeTierTeaserMapper;
    }

    /**
     * Save a pledgeTierTeaser.
     *
     * @param pledgeTierTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public PledgeTierTeaserDTO save(PledgeTierTeaserDTO pledgeTierTeaserDTO) {
        LOG.debug("Request to save PledgeTierTeaser : {}", pledgeTierTeaserDTO);
        PledgeTierTeaser pledgeTierTeaser = pledgeTierTeaserMapper.toEntity(pledgeTierTeaserDTO);
        pledgeTierTeaser = pledgeTierTeaserRepository.save(pledgeTierTeaser);
        return pledgeTierTeaserMapper.toDto(pledgeTierTeaser);
    }

    /**
     * Update a pledgeTierTeaser.
     *
     * @param pledgeTierTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public PledgeTierTeaserDTO update(PledgeTierTeaserDTO pledgeTierTeaserDTO) {
        LOG.debug("Request to update PledgeTierTeaser : {}", pledgeTierTeaserDTO);
        PledgeTierTeaser pledgeTierTeaser = pledgeTierTeaserMapper.toEntity(pledgeTierTeaserDTO);
        pledgeTierTeaser = pledgeTierTeaserRepository.save(pledgeTierTeaser);
        return pledgeTierTeaserMapper.toDto(pledgeTierTeaser);
    }

    /**
     * Partially update a pledgeTierTeaser.
     *
     * @param pledgeTierTeaserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PledgeTierTeaserDTO> partialUpdate(PledgeTierTeaserDTO pledgeTierTeaserDTO) {
        LOG.debug("Request to partially update PledgeTierTeaser : {}", pledgeTierTeaserDTO);

        return pledgeTierTeaserRepository
            .findById(pledgeTierTeaserDTO.getId())
            .map(existingPledgeTierTeaser -> {
                pledgeTierTeaserMapper.partialUpdate(existingPledgeTierTeaser, pledgeTierTeaserDTO);

                return existingPledgeTierTeaser;
            })
            .map(pledgeTierTeaserRepository::save)
            .map(pledgeTierTeaserMapper::toDto);
    }

    /**
     * Get all the pledgeTierTeasers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PledgeTierTeaserDTO> findAll() {
        LOG.debug("Request to get all PledgeTierTeasers");
        return pledgeTierTeaserRepository
            .findAll()
            .stream()
            .map(pledgeTierTeaserMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one pledgeTierTeaser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PledgeTierTeaserDTO> findOne(Long id) {
        LOG.debug("Request to get PledgeTierTeaser : {}", id);
        return pledgeTierTeaserRepository.findById(id).map(pledgeTierTeaserMapper::toDto);
    }

    /**
     * Delete the pledgeTierTeaser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PledgeTierTeaser : {}", id);
        pledgeTierTeaserRepository.deleteById(id);
    }
}
