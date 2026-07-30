package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.ServiceHighlight;
import net.jojoaddison.abofonsa.preview.repository.ServiceHighlightRepository;
import net.jojoaddison.abofonsa.preview.service.dto.ServiceHighlightDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.ServiceHighlightMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.ServiceHighlight}.
 */
@Service
@Transactional
public class ServiceHighlightService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHighlightService.class);

    private final ServiceHighlightRepository serviceHighlightRepository;

    private final ServiceHighlightMapper serviceHighlightMapper;

    public ServiceHighlightService(ServiceHighlightRepository serviceHighlightRepository, ServiceHighlightMapper serviceHighlightMapper) {
        this.serviceHighlightRepository = serviceHighlightRepository;
        this.serviceHighlightMapper = serviceHighlightMapper;
    }

    /**
     * Save a serviceHighlight.
     *
     * @param serviceHighlightDTO the entity to save.
     * @return the persisted entity.
     */
    public ServiceHighlightDTO save(ServiceHighlightDTO serviceHighlightDTO) {
        LOG.debug("Request to save ServiceHighlight : {}", serviceHighlightDTO);
        ServiceHighlight serviceHighlight = serviceHighlightMapper.toEntity(serviceHighlightDTO);
        serviceHighlight = serviceHighlightRepository.save(serviceHighlight);
        return serviceHighlightMapper.toDto(serviceHighlight);
    }

    /**
     * Update a serviceHighlight.
     *
     * @param serviceHighlightDTO the entity to save.
     * @return the persisted entity.
     */
    public ServiceHighlightDTO update(ServiceHighlightDTO serviceHighlightDTO) {
        LOG.debug("Request to update ServiceHighlight : {}", serviceHighlightDTO);
        ServiceHighlight serviceHighlight = serviceHighlightMapper.toEntity(serviceHighlightDTO);
        serviceHighlight = serviceHighlightRepository.save(serviceHighlight);
        return serviceHighlightMapper.toDto(serviceHighlight);
    }

    /**
     * Partially update a serviceHighlight.
     *
     * @param serviceHighlightDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ServiceHighlightDTO> partialUpdate(ServiceHighlightDTO serviceHighlightDTO) {
        LOG.debug("Request to partially update ServiceHighlight : {}", serviceHighlightDTO);

        return serviceHighlightRepository
            .findById(serviceHighlightDTO.getId())
            .map(existingServiceHighlight -> {
                serviceHighlightMapper.partialUpdate(existingServiceHighlight, serviceHighlightDTO);

                return existingServiceHighlight;
            })
            .map(serviceHighlightRepository::save)
            .map(serviceHighlightMapper::toDto);
    }

    /**
     * Get all the serviceHighlights.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ServiceHighlightDTO> findAll() {
        LOG.debug("Request to get all ServiceHighlights");
        return serviceHighlightRepository
            .findAll()
            .stream()
            .map(serviceHighlightMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the serviceHighlights with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ServiceHighlightDTO> findAllWithEagerRelationships(Pageable pageable) {
        return serviceHighlightRepository.findAllWithEagerRelationships(pageable).map(serviceHighlightMapper::toDto);
    }

    /**
     * Get one serviceHighlight by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ServiceHighlightDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceHighlight : {}", id);
        return serviceHighlightRepository.findOneWithEagerRelationships(id).map(serviceHighlightMapper::toDto);
    }

    /**
     * Delete the serviceHighlight by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ServiceHighlight : {}", id);
        serviceHighlightRepository.deleteById(id);
    }
}
