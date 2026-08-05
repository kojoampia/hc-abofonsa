package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.jojoaddison.abofonsa.preview.service.WaitlistCaptureService;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistReceiptDTO;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSubmissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public PublicWaitlistResource(WaitlistCaptureService waitlistCaptureService) {
        this.waitlistCaptureService = waitlistCaptureService;
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
     * {@code POST /api/public/waitlist/confirm} : complete double opt-in.
     *
     * <p>POST, and reached from a button on {@code /confirm} rather than directly from the emailed
     * link. Both of these were side-effecting {@code GET}s answered straight from the URL in the
     * message, and mail clients and security gateways routinely prefetch links — so a scanner could
     * confirm a signup the recipient never clicked, which quietly turns the consent record into a
     * record of what a robot did. The emailed link now lands on a page that asks.
     *
     * <p>That page is also the fix for a plain bug: the link has always pointed at {@code /confirm},
     * and the Angular router had routes for {@code /confirmed} and {@code /unsubscribed} but none
     * for {@code /confirm}, so every confirmation link led to the 404 page.
     */
    @PostMapping("/confirm")
    public ResponseEntity<OptInResultDTO> confirm(@Valid @RequestBody TokenDTO body, HttpServletRequest request) {
        LOG.debug("REST request to confirm a waitlist signup");
        boolean confirmed = waitlistCaptureService.confirm(body.token(), request).isPresent();
        return ResponseEntity.ok(new OptInResultDTO(confirmed ? "ok" : "invalid"));
    }

    /** {@code POST /api/public/waitlist/unsubscribe} : opt out, from a link in an email. */
    @PostMapping("/unsubscribe")
    public ResponseEntity<OptInResultDTO> unsubscribe(@Valid @RequestBody TokenDTO body) {
        LOG.debug("REST request to unsubscribe from the waitlist");
        boolean removed = waitlistCaptureService.unsubscribe(body.token()).isPresent();
        return ResponseEntity.ok(new OptInResultDTO(removed ? "ok" : "invalid"));
    }

    /** The opt-in or opt-out secret, posted as a body rather than carried in a URL. */
    public record TokenDTO(@NotBlank @Size(max = 64) String token) {}

    /**
     * {@code ok} or {@code invalid}, and nothing else.
     *
     * <p>No distinction between "no such token", "already used" and "expired": each of those is a
     * fact about somebody else's subscription, and answering them turns this endpoint into a way to
     * test whether a given token — or, by extension, a given address — is on the list.
     */
    public record OptInResultDTO(String status) {}
}
