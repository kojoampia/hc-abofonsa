package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.LaunchMilestone;
import net.jojoaddison.abofonsa.preview.repository.LaunchMilestoneRepository;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchMilestoneDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.LaunchMilestoneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.LaunchMilestone}.
 */
@Service
@Transactional
public class LaunchMilestoneService {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchMilestoneService.class);

    private final LaunchMilestoneRepository launchMilestoneRepository;

    private final LaunchMilestoneMapper launchMilestoneMapper;

    public LaunchMilestoneService(LaunchMilestoneRepository launchMilestoneRepository, LaunchMilestoneMapper launchMilestoneMapper) {
        this.launchMilestoneRepository = launchMilestoneRepository;
        this.launchMilestoneMapper = launchMilestoneMapper;
    }

    /**
     * Save a launchMilestone.
     *
     * @param launchMilestoneDTO the entity to save.
     * @return the persisted entity.
     */
    public LaunchMilestoneDTO save(LaunchMilestoneDTO launchMilestoneDTO) {
        LOG.debug("Request to save LaunchMilestone : {}", launchMilestoneDTO);
        LaunchMilestone launchMilestone = launchMilestoneMapper.toEntity(launchMilestoneDTO);
        launchMilestone = launchMilestoneRepository.save(launchMilestone);
        return launchMilestoneMapper.toDto(launchMilestone);
    }

    /**
     * Update a launchMilestone.
     *
     * @param launchMilestoneDTO the entity to save.
     * @return the persisted entity.
     */
    public LaunchMilestoneDTO update(LaunchMilestoneDTO launchMilestoneDTO) {
        LOG.debug("Request to update LaunchMilestone : {}", launchMilestoneDTO);
        LaunchMilestone launchMilestone = launchMilestoneMapper.toEntity(launchMilestoneDTO);
        launchMilestone = launchMilestoneRepository.save(launchMilestone);
        return launchMilestoneMapper.toDto(launchMilestone);
    }

    /**
     * Partially update a launchMilestone.
     *
     * @param launchMilestoneDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LaunchMilestoneDTO> partialUpdate(LaunchMilestoneDTO launchMilestoneDTO) {
        LOG.debug("Request to partially update LaunchMilestone : {}", launchMilestoneDTO);

        return launchMilestoneRepository
            .findById(launchMilestoneDTO.getId())
            .map(existingLaunchMilestone -> {
                launchMilestoneMapper.partialUpdate(existingLaunchMilestone, launchMilestoneDTO);

                return existingLaunchMilestone;
            })
            .map(launchMilestoneRepository::save)
            .map(launchMilestoneMapper::toDto);
    }

    /**
     * Get all the launchMilestones.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaunchMilestoneDTO> findAll() {
        LOG.debug("Request to get all LaunchMilestones");
        return launchMilestoneRepository
            .findAll()
            .stream()
            .map(launchMilestoneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one launchMilestone by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LaunchMilestoneDTO> findOne(Long id) {
        LOG.debug("Request to get LaunchMilestone : {}", id);
        return launchMilestoneRepository.findById(id).map(launchMilestoneMapper::toDto);
    }

    /**
     * Delete the launchMilestone by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LaunchMilestone : {}", id);
        launchMilestoneRepository.deleteById(id);
    }
}
