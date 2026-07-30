package net.jojoaddison.abofonsa.preview.service;

import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import net.jojoaddison.abofonsa.preview.repository.DataExportLogRepository;
import net.jojoaddison.abofonsa.preview.service.dto.DataExportLogDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.DataExportLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.DataExportLog}.
 */
@Service
@Transactional
public class DataExportLogService {

    private static final Logger LOG = LoggerFactory.getLogger(DataExportLogService.class);

    private final DataExportLogRepository dataExportLogRepository;

    private final DataExportLogMapper dataExportLogMapper;

    public DataExportLogService(DataExportLogRepository dataExportLogRepository, DataExportLogMapper dataExportLogMapper) {
        this.dataExportLogRepository = dataExportLogRepository;
        this.dataExportLogMapper = dataExportLogMapper;
    }

    /**
     * Save a dataExportLog.
     *
     * @param dataExportLogDTO the entity to save.
     * @return the persisted entity.
     */
    public DataExportLogDTO save(DataExportLogDTO dataExportLogDTO) {
        LOG.debug("Request to save DataExportLog : {}", dataExportLogDTO);
        DataExportLog dataExportLog = dataExportLogMapper.toEntity(dataExportLogDTO);
        dataExportLog = dataExportLogRepository.save(dataExportLog);
        return dataExportLogMapper.toDto(dataExportLog);
    }

    /**
     * Update a dataExportLog.
     *
     * @param dataExportLogDTO the entity to save.
     * @return the persisted entity.
     */
    public DataExportLogDTO update(DataExportLogDTO dataExportLogDTO) {
        LOG.debug("Request to update DataExportLog : {}", dataExportLogDTO);
        DataExportLog dataExportLog = dataExportLogMapper.toEntity(dataExportLogDTO);
        dataExportLog = dataExportLogRepository.save(dataExportLog);
        return dataExportLogMapper.toDto(dataExportLog);
    }

    /**
     * Partially update a dataExportLog.
     *
     * @param dataExportLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DataExportLogDTO> partialUpdate(DataExportLogDTO dataExportLogDTO) {
        LOG.debug("Request to partially update DataExportLog : {}", dataExportLogDTO);

        return dataExportLogRepository
            .findById(dataExportLogDTO.getId())
            .map(existingDataExportLog -> {
                dataExportLogMapper.partialUpdate(existingDataExportLog, dataExportLogDTO);

                return existingDataExportLog;
            })
            .map(dataExportLogRepository::save)
            .map(dataExportLogMapper::toDto);
    }

    /**
     * Get one dataExportLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DataExportLogDTO> findOne(Long id) {
        LOG.debug("Request to get DataExportLog : {}", id);
        return dataExportLogRepository.findById(id).map(dataExportLogMapper::toDto);
    }

    /**
     * Delete the dataExportLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DataExportLog : {}", id);
        dataExportLogRepository.deleteById(id);
    }
}
