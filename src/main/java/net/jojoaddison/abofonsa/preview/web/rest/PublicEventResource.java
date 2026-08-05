package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.EnumSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.service.CaptureEventRecorder;
import net.jojoaddison.abofonsa.preview.service.RequestThrottleService;
import net.jojoaddison.abofonsa.preview.service.VisitorContextService;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * The launch page's analytics beacon, unauthenticated.
 */
@RestController
@RequestMapping("/api/public/events")
public class PublicEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicEventResource.class);

    /**
     * What the browser is allowed to assert.
     *
     * <p>An allow-list, not a deny-list: a new event type added to the enum should have to be
     * considered before the public internet can post it. {@code WAITLIST_SUBMIT},
     * {@code WAITLIST_CONFIRM} and {@code WAITLIST_DUPLICATE} are absent because the server writes
     * those itself when the thing actually happens — accepting them here would let anyone inflate
     * the signup count without signing up, which is the one number this dashboard exists to report.
     */
    private static final Set<CaptureEventType> BROWSER_REPORTABLE = EnumSet.of(
        CaptureEventType.PAGE_VIEW,
        CaptureEventType.PLEDGE_CTA_CLICK,
        CaptureEventType.SERVICE_VIEW,
        CaptureEventType.PLAN_VIEW,
        CaptureEventType.CONTACT_CLICK,
        CaptureEventType.SOCIAL_CLICK
    );

    private final CaptureEventRecorder captureEventRecorder;
    private final RequestThrottleService requestThrottleService;
    private final VisitorContextService visitorContextService;

    public PublicEventResource(
        CaptureEventRecorder captureEventRecorder,
        RequestThrottleService requestThrottleService,
        VisitorContextService visitorContextService
    ) {
        this.captureEventRecorder = captureEventRecorder;
        this.requestThrottleService = requestThrottleService;
        this.visitorContextService = visitorContextService;
    }

    /**
     * {@code POST /api/public/events} : record one interaction.
     *
     * <p>204, with no body: this is called from {@code navigator.sendBeacon} during page unload,
     * where nothing is listening for a response and returning one is wasted bytes.
     */
    @PostMapping
    public ResponseEntity<Void> record(@Valid @RequestBody CaptureEventRequestDTO request, HttpServletRequest httpRequest) {
        if (!BROWSER_REPORTABLE.contains(request.eventType())) {
            LOG.debug("Refused a browser-reported {} event", request.eventType());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventType is not reportable from the browser");
        }
        // Anonymous, and one row written per call. The event-type allow-list above stops the counts
        // that matter being forged, but nothing stopped the volume: an unauthenticated client could
        // grow capture_event without bound on a single small Postgres, and drown the campaign-source
        // split in invented traffic. The cap is generous — a real visit fires a handful of these —
        // so a browser will never see it and a script will.
        if (!requestThrottleService.beaconAllowed(visitorContextService.ipHash(httpRequest))) {
            LOG.debug("Throttled a browser-reported {} event", request.eventType());
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many events from this device.");
        }
        captureEventRecorder.record(request, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
