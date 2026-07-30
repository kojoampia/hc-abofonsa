package net.jojoaddison.abofonsa.preview.web.rest;

import java.time.Duration;
import net.jojoaddison.abofonsa.preview.service.PublicContentService;
import net.jojoaddison.abofonsa.preview.service.dto.PublicContentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The launch page's content, unauthenticated.
 */
@RestController
@RequestMapping("/api/public")
public class PublicContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicContentResource.class);

    private final PublicContentService publicContentService;

    public PublicContentResource(PublicContentService publicContentService) {
        this.publicContentService = publicContentService;
    }

    /**
     * {@code GET /api/public/content} : everything the launch page renders.
     *
     * <p>Served with a short public cache lifetime so a burst of traffic on announcement day is
     * absorbed by any CDN or reverse proxy in front of this app rather than by Postgres. Sixty
     * seconds is chosen to be shorter than anyone's patience after editing a content row.
     */
    @GetMapping("/content")
    public ResponseEntity<PublicContentDTO> getContent() {
        LOG.debug("REST request for the public launch content");
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic())
            .body(publicContentService.getContent());
    }
}
