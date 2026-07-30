package net.jojoaddison.abofonsa.preview.service;

import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.MetricRollupDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.MetricRollupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.MetricRollup}.
 */
@Service
@Transactional
public class MetricRollupService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRollupService.class);

    private final MetricRollupRepository metricRollupRepository;

    private final MetricRollupMapper metricRollupMapper;

    public MetricRollupService(MetricRollupRepository metricRollupRepository, MetricRollupMapper metricRollupMapper) {
        this.metricRollupRepository = metricRollupRepository;
        this.metricRollupMapper = metricRollupMapper;
    }

    /**
     * Save a metricRollup.
     *
     * @param metricRollupDTO the entity to save.
     * @return the persisted entity.
     */
    public MetricRollupDTO save(MetricRollupDTO metricRollupDTO) {
        LOG.debug("Request to save MetricRollup : {}", metricRollupDTO);
        MetricRollup metricRollup = metricRollupMapper.toEntity(metricRollupDTO);
        metricRollup = metricRollupRepository.save(metricRollup);
        return metricRollupMapper.toDto(metricRollup);
    }

    /**
     * Update a metricRollup.
     *
     * @param metricRollupDTO the entity to save.
     * @return the persisted entity.
     */
    public MetricRollupDTO update(MetricRollupDTO metricRollupDTO) {
        LOG.debug("Request to update MetricRollup : {}", metricRollupDTO);
        MetricRollup metricRollup = metricRollupMapper.toEntity(metricRollupDTO);
        metricRollup = metricRollupRepository.save(metricRollup);
        return metricRollupMapper.toDto(metricRollup);
    }

    /**
     * Partially update a metricRollup.
     *
     * @param metricRollupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MetricRollupDTO> partialUpdate(MetricRollupDTO metricRollupDTO) {
        LOG.debug("Request to partially update MetricRollup : {}", metricRollupDTO);

        return metricRollupRepository
            .findById(metricRollupDTO.getId())
            .map(existingMetricRollup -> {
                metricRollupMapper.partialUpdate(existingMetricRollup, metricRollupDTO);

                return existingMetricRollup;
            })
            .map(metricRollupRepository::save)
            .map(metricRollupMapper::toDto);
    }

    /**
     * Get one metricRollup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MetricRollupDTO> findOne(Long id) {
        LOG.debug("Request to get MetricRollup : {}", id);
        return metricRollupRepository.findById(id).map(metricRollupMapper::toDto);
    }

    /**
     * Delete the metricRollup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MetricRollup : {}", id);
        metricRollupRepository.deleteById(id);
    }
}
