package net.jojoaddison.abofonsa.preview.service;

import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistReceiptDTO;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSubmissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Captures waitlist addresses: normalise, deduplicate, issue an opt-in token, count the event.
 *
 * <p>Named apart from the generated {@code WaitlistSignupService}, which is the entity's admin CRUD
 * service. This is the public capture path and enforces rules that do not apply to an
 * administrator editing a row.
 */
@Service
@Transactional
public class WaitlistCaptureService {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistCaptureService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Below this, the submission did not come from somebody reading the page. Generous on purpose:
     * a fast typist with autofill can be well under two seconds, and a false rejection here costs a
     * real signup.
     */
    private static final Duration MIN_DWELL = Duration.ofMillis(800);

    private final WaitlistSignupRepository repository;
    private final VisitorContextService visitorContext;
    private final CaptureEventRecorder eventRecorder;
    private final MailService mailService;
    private final String publicBaseUrl;
    private final int maxPerHourPerClient;

    public WaitlistCaptureService(
        WaitlistSignupRepository repository,
        VisitorContextService visitorContext,
        CaptureEventRecorder eventRecorder,
        MailService mailService,
        @Value("${abofonsa.public-base-url:http://localhost:8083}") String publicBaseUrl,
        @Value("${abofonsa.waitlist.max-per-hour-per-client:5}") int maxPerHourPerClient
    ) {
        this.repository = repository;
        this.visitorContext = visitorContext;
        this.eventRecorder = eventRecorder;
        this.mailService = mailService;
        this.publicBaseUrl = publicBaseUrl;
        this.maxPerHourPerClient = maxPerHourPerClient;
    }

    public WaitlistReceiptDTO submit(WaitlistSubmissionDTO submission, HttpServletRequest request) {
        rejectBots(submission);

        String normalized = normalise(submission.email());
        String ipHash = visitorContext.ipHash(request);

        if (repository.countByIpHashAndCapturedAtAfter(ipHash, Instant.now().minus(Duration.ofHours(1))) >= maxPerHourPerClient) {
            throw new WaitlistThrottledException();
        }

        // map(...) rather than isPresent()/get() — the modernizer plugin rejects the latter, and
        // this reads better anyway.
        Optional<WaitlistReceiptDTO> duplicate = repository
            .findByEmailNormalized(normalized)
            .map(existing -> handleExisting(existing, request));
        if (duplicate.isPresent()) {
            return duplicate.orElseThrow();
        }

        WaitlistSignup signup = new WaitlistSignup();
        signup.setEmail(submission.email().trim());
        signup.setEmailNormalized(normalized);
        signup.setFullName(blankToNull(submission.fullName()));
        signup.setOrganisation(blankToNull(submission.organisation()));
        signup.setAudience(submission.audience());
        signup.setPlanOfInterest(submission.planOfInterest());
        signup.setStatus(SignupStatus.PENDING);
        signup.setLocale(blankToNull(submission.locale()));
        signup.setSourcePage(blankToNull(submission.sourcePage()));
        signup.setUtmSource(blankToNull(submission.utmSource()));
        signup.setUtmMedium(blankToNull(submission.utmMedium()));
        signup.setUtmCampaign(blankToNull(submission.utmCampaign()));
        signup.setReferrer(visitorContext.referrerHost(request));
        signup.setDeviceType(visitorContext.deviceType(request));
        signup.setConsentGiven(true);
        signup.setConfirmationToken(newToken());
        signup.setCapturedAt(Instant.now());
        signup.setIpHash(ipHash);
        signup.setUserAgent(visitorContext.userAgent(request));

        WaitlistSignup saved = repository.save(signup);
        eventRecorder.recordServerSide(CaptureEventType.WAITLIST_SUBMIT, request, saved.getLocale(), saved.getSourcePage(), null);
        sendConfirmation(saved);

        return new WaitlistReceiptDTO(
            saved.getConfirmationToken(),
            WaitlistReceiptDTO.Status.PENDING_CONFIRMATION,
            false,
            saved.getCapturedAt()
        );
    }

    /** Completes double opt-in. Idempotent: clicking the link twice is a success, not an error. */
    public Optional<WaitlistSignup> confirm(String token, HttpServletRequest request) {
        return repository.findByConfirmationToken(token).map(signup -> {
            if (signup.getStatus() != SignupStatus.CONFIRMED) {
                signup.setStatus(SignupStatus.CONFIRMED);
                signup.setConfirmedAt(Instant.now());
                repository.save(signup);
                eventRecorder.recordServerSide(CaptureEventType.WAITLIST_CONFIRM, request, signup.getLocale(), null, null);
            }
            return signup;
        });
    }

    /**
     * Honours an unsubscribe. The row is kept rather than deleted, so the address stays recognisable
     * as one that asked to be left alone; an actual erasure request is a separate, deliberate
     * action taken from the admin side.
     */
    public Optional<WaitlistSignup> unsubscribe(String token) {
        return repository.findByConfirmationToken(token).map(signup -> {
            if (signup.getStatus() != SignupStatus.UNSUBSCRIBED) {
                signup.setStatus(SignupStatus.UNSUBSCRIBED);
                signup.setUnsubscribedAt(Instant.now());
                repository.save(signup);
            }
            return signup;
        });
    }

    private WaitlistReceiptDTO handleExisting(WaitlistSignup existing, HttpServletRequest request) {
        eventRecorder.recordServerSide(CaptureEventType.WAITLIST_DUPLICATE, request, existing.getLocale(), null, null);

        if (existing.getStatus() == SignupStatus.CONFIRMED) {
            return new WaitlistReceiptDTO(null, WaitlistReceiptDTO.Status.ALREADY_CONFIRMED, true, existing.getCapturedAt());
        }

        // Pending, unsubscribed or bounced: re-issuing the link is the useful behaviour. Somebody
        // who unsubscribed and has now signed up again has plainly changed their mind, and the
        // opt-in click is what actually puts them back on the list.
        if (existing.getConfirmationToken() == null) {
            existing.setConfirmationToken(newToken());
        }
        existing.setStatus(SignupStatus.PENDING);
        repository.save(existing);
        sendConfirmation(existing);

        return new WaitlistReceiptDTO(
            existing.getConfirmationToken(),
            WaitlistReceiptDTO.Status.PENDING_CONFIRMATION,
            true,
            existing.getCapturedAt()
        );
    }

    private void rejectBots(WaitlistSubmissionDTO submission) {
        if (submission.company() != null && !submission.company().isBlank()) {
            LOG.debug("Rejected a waitlist submission that filled the honeypot field");
            throw new WaitlistRejectedException("honeypot");
        }
        if (submission.dwellMs() > 0 && submission.dwellMs() < MIN_DWELL.toMillis()) {
            LOG.debug("Rejected a waitlist submission after {}ms on the page", submission.dwellMs());
            throw new WaitlistRejectedException("dwell");
        }
    }

    private void sendConfirmation(WaitlistSignup signup) {
        String link = publicBaseUrl + "/confirm?token=" + signup.getConfirmationToken();
        try {
            mailService.sendEmail(
                signup.getEmail(),
                "Confirm your place on the Health Connect waitlist",
                "Please confirm your email address by opening this link:\n\n" +
                    link +
                    "\n\nIf you did not request this, ignore this message and nothing further will happen.",
                false,
                false
            );
        } catch (RuntimeException e) {
            // A capture that reached the database is a success even when the mail server is not
            // configured. Failing the request here would lose a real signup over a delivery problem
            // the person submitting it can do nothing about.
            //
            // Message at WARN, stack trace at DEBUG: with no SMTP server configured this fires on
            // every signup, and a full trace each time buries the line that actually matters —
            // the opt-in link, which is the only way to complete the flow in that situation.
            LOG.warn("Could not send the confirmation email ({}); the opt-in link is {}", e.getMessage(), link);
            LOG.debug("Confirmation email failure detail", e);
        }
    }

    private static String normalise(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String newToken() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /**
     * Submission looked automated. 400 rather than 429 — there is nothing to retry.
     *
     * <p>Both of these extend {@link ResponseStatusException} so that JHipster's
     * {@code ExceptionTranslator}, whose catch-all would otherwise render them as a 500, picks up
     * the intended status: it special-cases {@code ErrorResponseException}, and
     * {@code ResponseStatusException} is one.
     *
     * <p>The reason is not echoed to the caller. Telling a bot which check it failed is telling it
     * what to change.
     */
    public static class WaitlistRejectedException extends ResponseStatusException {

        public WaitlistRejectedException(String reason) {
            super(HttpStatus.BAD_REQUEST, "That submission could not be accepted.");
            this.reason = reason;
        }

        private final transient String reason;

        public String getReason() {
            return reason;
        }
    }

    /** Too many submissions from one client. */
    public static class WaitlistThrottledException extends ResponseStatusException {

        public WaitlistThrottledException() {
            super(HttpStatus.TOO_MANY_REQUESTS, "Too many submissions from this device. Please try again shortly.");
        }
    }
}
