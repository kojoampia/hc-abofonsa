package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import net.jojoaddison.abofonsa.preview.repository.LaunchSettingRepository;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchSettingDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.LaunchSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.LaunchSetting}.
 */
@Service
@Transactional
public class LaunchSettingService {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchSettingService.class);

    private final LaunchSettingRepository launchSettingRepository;

    private final LaunchSettingMapper launchSettingMapper;

    public LaunchSettingService(LaunchSettingRepository launchSettingRepository, LaunchSettingMapper launchSettingMapper) {
        this.launchSettingRepository = launchSettingRepository;
        this.launchSettingMapper = launchSettingMapper;
    }

    /**
     * Save a launchSetting.
     *
     * @param launchSettingDTO the entity to save.
     * @return the persisted entity.
     */
    public LaunchSettingDTO save(LaunchSettingDTO launchSettingDTO) {
        LOG.debug("Request to save LaunchSetting : {}", launchSettingDTO);
        LaunchSetting launchSetting = launchSettingMapper.toEntity(launchSettingDTO);
        launchSetting = launchSettingRepository.save(launchSetting);
        return launchSettingMapper.toDto(launchSetting);
    }

    /**
     * Update a launchSetting.
     *
     * @param launchSettingDTO the entity to save.
     * @return the persisted entity.
     */
    public LaunchSettingDTO update(LaunchSettingDTO launchSettingDTO) {
        LOG.debug("Request to update LaunchSetting : {}", launchSettingDTO);
        LaunchSetting launchSetting = launchSettingMapper.toEntity(launchSettingDTO);
        launchSetting = launchSettingRepository.save(launchSetting);
        return launchSettingMapper.toDto(launchSetting);
    }

    /**
     * Partially update a launchSetting.
     *
     * @param launchSettingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LaunchSettingDTO> partialUpdate(LaunchSettingDTO launchSettingDTO) {
        LOG.debug("Request to partially update LaunchSetting : {}", launchSettingDTO);

        return launchSettingRepository
            .findById(launchSettingDTO.getId())
            .map(existingLaunchSetting -> {
                launchSettingMapper.partialUpdate(existingLaunchSetting, launchSettingDTO);

                return existingLaunchSetting;
            })
            .map(launchSettingRepository::save)
            .map(launchSettingMapper::toDto);
    }

    /**
     * Get all the launchSettings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaunchSettingDTO> findAll() {
        LOG.debug("Request to get all LaunchSettings");
        return launchSettingRepository.findAll().stream().map(launchSettingMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one launchSetting by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LaunchSettingDTO> findOne(Long id) {
        LOG.debug("Request to get LaunchSetting : {}", id);
        return launchSettingRepository.findById(id).map(launchSettingMapper::toDto);
    }

    /**
     * Delete the launchSetting by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LaunchSetting : {}", id);
        launchSettingRepository.deleteById(id);
    }
}
