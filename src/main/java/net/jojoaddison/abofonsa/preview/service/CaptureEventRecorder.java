package net.jojoaddison.abofonsa.preview.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneOffset;
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes the append-only event log every metric is derived from.
 *
 * <p>Named "Recorder" rather than "Service" because {@code CaptureEventService} already exists —
 * that is JHipster's generated CRUD service for the entity, which the admin screens use. This is
 * the ingestion path, and the two have nothing in common beyond the table.
 *
 * <p>{@code occurredDate} is stored alongside {@code occurredAt} rather than derived at query time:
 * a {@code date_trunc} in the GROUP BY cannot use an index on the instant, and the day/week/month
 * drill-downs are the dashboard's whole job.
 */
@Service
@Transactional
public class CaptureEventRecorder {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureEventRecorder.class);

    private final CaptureEventRepository captureEventRepository;
    private final VisitorContextService visitorContext;

    public CaptureEventRecorder(CaptureEventRepository captureEventRepository, VisitorContextService visitorContext) {
        this.captureEventRepository = captureEventRepository;
        this.visitorContext = visitorContext;
    }

    /** Records an event the browser reported. */
    public void record(CaptureEventRequestDTO request, HttpServletRequest httpRequest) {
        CaptureEvent event = base(request.eventType(), httpRequest);
        event.setLocale(trim(request.locale(), 10));
        event.setSourcePage(trim(request.sourcePage(), 255));
        event.setUtmSource(trim(request.utmSource(), 120));
        event.setUtmMedium(trim(request.utmMedium(), 120));
        event.setUtmCampaign(trim(request.utmCampaign(), 120));
        event.setTargetKey(trim(request.targetKey(), 120));
        captureEventRepository.save(event);
        LOG.debug("Recorded {} event", request.eventType());
    }

    /**
     * Records an event the server observed — a waitlist submission that actually succeeded, an
     * opt-in link that was actually clicked.
     *
     * <p>Separate from {@link #record} so these cannot be forged: the public beacon refuses the
     * server-attested event types, so the only way to increment the signup count is to sign up.
     */
    public void recordServerSide(
        CaptureEventType type,
        HttpServletRequest httpRequest,
        String locale,
        String sourcePage,
        String targetKey
    ) {
        CaptureEvent event = base(type, httpRequest);
        event.setLocale(trim(locale, 10));
        event.setSourcePage(trim(sourcePage, 255));
        event.setTargetKey(trim(targetKey, 120));
        captureEventRepository.save(event);
        LOG.debug("Recorded server-side {} event", type);
    }

    private CaptureEvent base(CaptureEventType type, HttpServletRequest httpRequest) {
        Instant now = Instant.now();
        CaptureEvent event = new CaptureEvent();
        event.setEventType(type);
        event.setOccurredAt(now);
        // UTC, matching the rollup buckets. Presenting these in Africa/Accra is the dashboard's job.
        event.setOccurredDate(now.atZone(ZoneOffset.UTC).toLocalDate());
        event.setSessionHash(visitorContext.sessionHash(httpRequest));
        event.setDeviceType(visitorContext.deviceType(httpRequest));
        event.setReferrerHost(visitorContext.referrerHost(httpRequest));
        return event;
    }

    private static String trim(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }
}
