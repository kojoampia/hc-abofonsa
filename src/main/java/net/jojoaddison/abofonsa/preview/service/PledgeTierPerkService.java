package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierPerkRepository;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierPerkDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.PledgeTierPerkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk}.
 */
@Service
@Transactional
public class PledgeTierPerkService {

    private static final Logger LOG = LoggerFactory.getLogger(PledgeTierPerkService.class);

    private final PledgeTierPerkRepository pledgeTierPerkRepository;

    private final PledgeTierPerkMapper pledgeTierPerkMapper;

    public PledgeTierPerkService(PledgeTierPerkRepository pledgeTierPerkRepository, PledgeTierPerkMapper pledgeTierPerkMapper) {
        this.pledgeTierPerkRepository = pledgeTierPerkRepository;
        this.pledgeTierPerkMapper = pledgeTierPerkMapper;
    }

    /**
     * Save a pledgeTierPerk.
     *
     * @param pledgeTierPerkDTO the entity to save.
     * @return the persisted entity.
     */
    public PledgeTierPerkDTO save(PledgeTierPerkDTO pledgeTierPerkDTO) {
        LOG.debug("Request to save PledgeTierPerk : {}", pledgeTierPerkDTO);
        PledgeTierPerk pledgeTierPerk = pledgeTierPerkMapper.toEntity(pledgeTierPerkDTO);
        pledgeTierPerk = pledgeTierPerkRepository.save(pledgeTierPerk);
        return pledgeTierPerkMapper.toDto(pledgeTierPerk);
    }

    /**
     * Update a pledgeTierPerk.
     *
     * @param pledgeTierPerkDTO the entity to save.
     * @return the persisted entity.
     */
    public PledgeTierPerkDTO update(PledgeTierPerkDTO pledgeTierPerkDTO) {
        LOG.debug("Request to update PledgeTierPerk : {}", pledgeTierPerkDTO);
        PledgeTierPerk pledgeTierPerk = pledgeTierPerkMapper.toEntity(pledgeTierPerkDTO);
        pledgeTierPerk = pledgeTierPerkRepository.save(pledgeTierPerk);
        return pledgeTierPerkMapper.toDto(pledgeTierPerk);
    }

    /**
     * Partially update a pledgeTierPerk.
     *
     * @param pledgeTierPerkDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PledgeTierPerkDTO> partialUpdate(PledgeTierPerkDTO pledgeTierPerkDTO) {
        LOG.debug("Request to partially update PledgeTierPerk : {}", pledgeTierPerkDTO);

        return pledgeTierPerkRepository
            .findById(pledgeTierPerkDTO.getId())
            .map(existingPledgeTierPerk -> {
                pledgeTierPerkMapper.partialUpdate(existingPledgeTierPerk, pledgeTierPerkDTO);

                return existingPledgeTierPerk;
            })
            .map(pledgeTierPerkRepository::save)
            .map(pledgeTierPerkMapper::toDto);
    }

    /**
     * Get all the pledgeTierPerks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PledgeTierPerkDTO> findAll() {
        LOG.debug("Request to get all PledgeTierPerks");
        return pledgeTierPerkRepository
            .findAll()
            .stream()
            .map(pledgeTierPerkMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the pledgeTierPerks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PledgeTierPerkDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pledgeTierPerkRepository.findAllWithEagerRelationships(pageable).map(pledgeTierPerkMapper::toDto);
    }

    /**
     * Get one pledgeTierPerk by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PledgeTierPerkDTO> findOne(Long id) {
        LOG.debug("Request to get PledgeTierPerk : {}", id);
        return pledgeTierPerkRepository.findOneWithEagerRelationships(id).map(pledgeTierPerkMapper::toDto);
    }

    /**
     * Delete the pledgeTierPerk by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PledgeTierPerk : {}", id);
        pledgeTierPerkRepository.deleteById(id);
    }
}
