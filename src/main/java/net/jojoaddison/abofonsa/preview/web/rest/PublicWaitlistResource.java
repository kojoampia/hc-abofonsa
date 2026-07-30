package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import net.jojoaddison.abofonsa.preview.service.WaitlistCaptureService;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistReceiptDTO;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSubmissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Waitlist capture and double opt-in, unauthenticated.
 */
@RestController
@RequestMapping("/api/public/waitlist")
public class PublicWaitlistResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicWaitlistResource.class);

    private final WaitlistCaptureService waitlistCaptureService;
    private final String publicBaseUrl;

    public PublicWaitlistResource(
        WaitlistCaptureService waitlistCaptureService,
        @Value("${abofonsa.public-base-url:http://localhost:8083}") String publicBaseUrl
    ) {
        this.waitlistCaptureService = waitlistCaptureService;
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * {@code POST /api/public/waitlist} : capture an address.
     *
     * <p>Answers 202 rather than 201: what the caller gets back is an acknowledgement that the
     * address was taken, but membership of the list is not settled until the opt-in link is
     * clicked, and there is no public resource at a URL for them to go and look at.
     */
    @PostMapping
    public ResponseEntity<WaitlistReceiptDTO> submit(@Valid @RequestBody WaitlistSubmissionDTO submission, HttpServletRequest request) {
        LOG.debug("REST request to join the waitlist");
        WaitlistReceiptDTO receipt = waitlistCaptureService.submit(submission, request);
        // The token is an opt-in secret sent by email; echoing it in the response would let anyone
        // who can post an address confirm it themselves and defeat the point of double opt-in.
        WaitlistReceiptDTO body = new WaitlistReceiptDTO(null, receipt.status(), receipt.alreadyRegistered(), receipt.receivedAt());
        return ResponseEntity.accepted().body(body);
    }

    /**
     * {@code GET /api/public/waitlist/confirm} : complete double opt-in.
     *
     * <p>Redirects rather than returning JSON, because this URL is opened by a human clicking a
     * link in an email client, and what should happen next is that they land on the page.
     */
    @GetMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token, HttpServletRequest request) {
        LOG.debug("REST request to confirm a waitlist signup");
        boolean confirmed = waitlistCaptureService.confirm(token, request).isPresent();
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .location(URI.create(publicBaseUrl + "/confirmed?status=" + (confirmed ? "ok" : "invalid")))
            .build();
    }

    /** {@code GET /api/public/waitlist/unsubscribe} : opt out, from a link in an email. */
    @GetMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestParam("token") String token) {
        LOG.debug("REST request to unsubscribe from the waitlist");
        boolean removed = waitlistCaptureService.unsubscribe(token).isPresent();
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .location(URI.create(publicBaseUrl + "/unsubscribed?status=" + (removed ? "ok" : "invalid")))
            .build();
    }
}
