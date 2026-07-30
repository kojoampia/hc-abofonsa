package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import net.jojoaddison.abofonsa.preview.repository.CarePlanTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CarePlanTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CarePlanTeaserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser}.
 */
@Service
@Transactional
public class CarePlanTeaserService {

    private static final Logger LOG = LoggerFactory.getLogger(CarePlanTeaserService.class);

    private final CarePlanTeaserRepository carePlanTeaserRepository;

    private final CarePlanTeaserMapper carePlanTeaserMapper;

    public CarePlanTeaserService(CarePlanTeaserRepository carePlanTeaserRepository, CarePlanTeaserMapper carePlanTeaserMapper) {
        this.carePlanTeaserRepository = carePlanTeaserRepository;
        this.carePlanTeaserMapper = carePlanTeaserMapper;
    }

    /**
     * Save a carePlanTeaser.
     *
     * @param carePlanTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public CarePlanTeaserDTO save(CarePlanTeaserDTO carePlanTeaserDTO) {
        LOG.debug("Request to save CarePlanTeaser : {}", carePlanTeaserDTO);
        CarePlanTeaser carePlanTeaser = carePlanTeaserMapper.toEntity(carePlanTeaserDTO);
        carePlanTeaser = carePlanTeaserRepository.save(carePlanTeaser);
        return carePlanTeaserMapper.toDto(carePlanTeaser);
    }

    /**
     * Update a carePlanTeaser.
     *
     * @param carePlanTeaserDTO the entity to save.
     * @return the persisted entity.
     */
    public CarePlanTeaserDTO update(CarePlanTeaserDTO carePlanTeaserDTO) {
        LOG.debug("Request to update CarePlanTeaser : {}", carePlanTeaserDTO);
        CarePlanTeaser carePlanTeaser = carePlanTeaserMapper.toEntity(carePlanTeaserDTO);
        carePlanTeaser = carePlanTeaserRepository.save(carePlanTeaser);
        return carePlanTeaserMapper.toDto(carePlanTeaser);
    }

    /**
     * Partially update a carePlanTeaser.
     *
     * @param carePlanTeaserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CarePlanTeaserDTO> partialUpdate(CarePlanTeaserDTO carePlanTeaserDTO) {
        LOG.debug("Request to partially update CarePlanTeaser : {}", carePlanTeaserDTO);

        return carePlanTeaserRepository
            .findById(carePlanTeaserDTO.getId())
            .map(existingCarePlanTeaser -> {
                carePlanTeaserMapper.partialUpdate(existingCarePlanTeaser, carePlanTeaserDTO);

                return existingCarePlanTeaser;
            })
            .map(carePlanTeaserRepository::save)
            .map(carePlanTeaserMapper::toDto);
    }

    /**
     * Get all the carePlanTeasers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CarePlanTeaserDTO> findAll() {
        LOG.debug("Request to get all CarePlanTeasers");
        return carePlanTeaserRepository
            .findAll()
            .stream()
            .map(carePlanTeaserMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one carePlanTeaser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CarePlanTeaserDTO> findOne(Long id) {
        LOG.debug("Request to get CarePlanTeaser : {}", id);
        return carePlanTeaserRepository.findById(id).map(carePlanTeaserMapper::toDto);
    }

    /**
     * Delete the carePlanTeaser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CarePlanTeaser : {}", id);
        carePlanTeaserRepository.deleteById(id);
    }
}
