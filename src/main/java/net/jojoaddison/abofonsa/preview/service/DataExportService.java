package net.jojoaddison.abofonsa.preview.service;

import java.time.Instant;
import java.util.List;
import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.*;
import net.jojoaddison.abofonsa.preview.repository.DataExportLogRepository;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSeriesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Renders exports and records that they happened.
 *
 * <p>The audit row is the point of routing exports through a service at all. This table holds
 * people's email addresses; who pulled a copy, when, over what range and how many rows, is
 * information worth having and impossible to reconstruct afterwards.
 */
@Service
public class DataExportService {

    private static final Logger LOG = LoggerFactory.getLogger(DataExportService.class);

    private static final String[] SIGNUP_HEADERS = {
        "email",
        "fullName",
        "organisation",
        "audience",
        "planOfInterest",
        "status",
        "locale",
        "utmSource",
        "utmMedium",
        "utmCampaign",
        "capturedAt",
        "confirmedAt",
        "unsubscribedAt",
    };

    private final WaitlistSignupRepository waitlistSignupRepository;
    private final DataExportLogRepository dataExportLogRepository;

    public DataExportService(WaitlistSignupRepository waitlistSignupRepository, DataExportLogRepository dataExportLogRepository) {
        this.waitlistSignupRepository = waitlistSignupRepository;
        this.dataExportLogRepository = dataExportLogRepository;
    }

    /** Captured emails as CSV, optionally narrowed by status and capture date. */
    @Transactional
    public String exportSignupsCsv(SignupStatus status, Instant from, Instant to, String requestedBy) {
        long startedAt = System.nanoTime();

        List<WaitlistSignup> rows = waitlistSignupRepository.findForExport(status, from, to);

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", SIGNUP_HEADERS)).append('\n');
        for (WaitlistSignup signup : rows) {
            csv.append(csvCell(signup.getEmail()))
                .append(',')
                .append(csvCell(signup.getFullName()))
                .append(',')
                .append(csvCell(signup.getOrganisation()))
                .append(',')
                .append(csvCell(name(signup.getAudience())))
                .append(',')
                .append(csvCell(name(signup.getPlanOfInterest())))
                .append(',')
                .append(csvCell(name(signup.getStatus())))
                .append(',')
                .append(csvCell(signup.getLocale()))
                .append(',')
                .append(csvCell(signup.getUtmSource()))
                .append(',')
                .append(csvCell(signup.getUtmMedium()))
                .append(',')
                .append(csvCell(signup.getUtmCampaign()))
                .append(',')
                .append(csvCell(text(signup.getCapturedAt())))
                .append(',')
                .append(csvCell(text(signup.getConfirmedAt())))
                .append(',')
                .append(csvCell(text(signup.getUnsubscribedAt())))
                .append('\n');
        }

        record(
            ExportKind.WAITLIST_EMAILS,
            ExportFormat.CSV,
            from,
            to,
            null,
            "status=" + (status == null ? "ANY" : status),
            rows.size(),
            requestedBy,
            startedAt
        );
        return csv.toString();
    }

    /** A metric series as CSV — the "export metrics" half of the drill-down. */
    @Transactional
    public String exportSeriesCsv(MetricSeriesDTO series, String requestedBy) {
        long startedAt = System.nanoTime();

        StringBuilder csv = new StringBuilder("bucketStart,bucketEnd,series,value\n");
        int rowCount = 0;
        for (MetricSeriesDTO.Series line : series.series()) {
            for (MetricSeriesDTO.Point point : line.points()) {
                csv.append(csvCell(point.bucketStart().toString()))
                    .append(',')
                    .append(csvCell(point.bucketEnd().toString()))
                    .append(',')
                    .append(csvCell(line.label()))
                    .append(',')
                    .append(point.value())
                    .append('\n');
                rowCount++;
            }
        }

        record(
            ExportKind.METRIC_ROLLUPS,
            ExportFormat.CSV,
            series.from(),
            series.to(),
            BucketType.valueOf(series.bucketType()),
            "metric=" + series.metricKey() + (series.dimensionName() == null ? "" : " dimension=" + series.dimensionName()),
            rowCount,
            requestedBy,
            startedAt
        );
        return csv.toString();
    }

    private void record(
        ExportKind kind,
        ExportFormat format,
        Instant from,
        Instant to,
        BucketType bucketType,
        String filterSummary,
        int rowCount,
        String requestedBy,
        long startedAtNanos
    ) {
        DataExportLog log = new DataExportLog();
        log.setExportKind(kind);
        log.setFormat(format);
        log.setRangeFrom(from);
        log.setRangeTo(to);
        log.setBucketType(bucketType);
        log.setFilterSummary(filterSummary);
        log.setRowCount(rowCount);
        log.setRequestedBy(requestedBy);
        log.setRequestedAt(Instant.now());
        log.setDurationMs((System.nanoTime() - startedAtNanos) / 1_000_000);
        dataExportLogRepository.save(log);
        LOG.info("{} exported {} rows as {} ({})", requestedBy, rowCount, kind, filterSummary);
    }

    /**
     * Escapes a CSV cell.
     *
     * <p>The leading-character guard is deliberate. A cell beginning {@code =}, {@code +}, {@code -}
     * or {@code @} is executed as a formula when the file is opened in Excel or Sheets, so an
     * attacker who can get a crafted string into a name field can attack whoever opens the export.
     * Prefixing an apostrophe neutralises it while leaving the value readable.
     */
    private static String csvCell(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value;
        if (!escaped.isEmpty() && "=+-@\t\r".indexOf(escaped.charAt(0)) >= 0) {
            escaped = "'" + escaped;
        }
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            escaped = '"' + escaped.replace("\"", "\"\"") + '"';
        }
        return escaped;
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static String text(Instant value) {
        return value == null ? null : value.toString();
    }
}
