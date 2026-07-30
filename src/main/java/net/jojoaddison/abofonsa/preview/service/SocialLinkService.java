package net.jojoaddison.abofonsa.preview.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.jojoaddison.abofonsa.preview.domain.SocialLink;
import net.jojoaddison.abofonsa.preview.repository.SocialLinkRepository;
import net.jojoaddison.abofonsa.preview.service.dto.SocialLinkDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.SocialLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link net.jojoaddison.abofonsa.preview.domain.SocialLink}.
 */
@Service
@Transactional
public class SocialLinkService {

    private static final Logger LOG = LoggerFactory.getLogger(SocialLinkService.class);

    private final SocialLinkRepository socialLinkRepository;

    private final SocialLinkMapper socialLinkMapper;

    public SocialLinkService(SocialLinkRepository socialLinkRepository, SocialLinkMapper socialLinkMapper) {
        this.socialLinkRepository = socialLinkRepository;
        this.socialLinkMapper = socialLinkMapper;
    }

    /**
     * Save a socialLink.
     *
     * @param socialLinkDTO the entity to save.
     * @return the persisted entity.
     */
    public SocialLinkDTO save(SocialLinkDTO socialLinkDTO) {
        LOG.debug("Request to save SocialLink : {}", socialLinkDTO);
        SocialLink socialLink = socialLinkMapper.toEntity(socialLinkDTO);
        socialLink = socialLinkRepository.save(socialLink);
        return socialLinkMapper.toDto(socialLink);
    }

    /**
     * Update a socialLink.
     *
     * @param socialLinkDTO the entity to save.
     * @return the persisted entity.
     */
    public SocialLinkDTO update(SocialLinkDTO socialLinkDTO) {
        LOG.debug("Request to update SocialLink : {}", socialLinkDTO);
        SocialLink socialLink = socialLinkMapper.toEntity(socialLinkDTO);
        socialLink = socialLinkRepository.save(socialLink);
        return socialLinkMapper.toDto(socialLink);
    }

    /**
     * Partially update a socialLink.
     *
     * @param socialLinkDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SocialLinkDTO> partialUpdate(SocialLinkDTO socialLinkDTO) {
        LOG.debug("Request to partially update SocialLink : {}", socialLinkDTO);

        return socialLinkRepository
            .findById(socialLinkDTO.getId())
            .map(existingSocialLink -> {
                socialLinkMapper.partialUpdate(existingSocialLink, socialLinkDTO);

                return existingSocialLink;
            })
            .map(socialLinkRepository::save)
            .map(socialLinkMapper::toDto);
    }

    /**
     * Get all the socialLinks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SocialLinkDTO> findAll() {
        LOG.debug("Request to get all SocialLinks");
        return socialLinkRepository.findAll().stream().map(socialLinkMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one socialLink by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SocialLinkDTO> findOne(Long id) {
        LOG.debug("Request to get SocialLink : {}", id);
        return socialLinkRepository.findById(id).map(socialLinkMapper::toDto);
    }

    /**
     * Delete the socialLink by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SocialLink : {}", id);
        socialLinkRepository.deleteById(id);
    }
}
