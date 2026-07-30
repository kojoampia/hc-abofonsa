package net.jojoaddison.abofonsa.preview.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Locale;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Derives the few request attributes the launch page records, in a form that cannot be turned back
 * into a person.
 *
 * <p>Both hashes are salted SHA-256 and truncated to 32 hex characters. The salt is configuration,
 * not a constant, so a leaked database cannot be re-identified by hashing the IPv4 space — which
 * without a secret salt takes minutes.
 */
@Service
public class VisitorContextService {

    /**
     * The session hash additionally mixes in the current UTC date, which makes it rotate at
     * midnight. That is what keeps UNIQUE_VISITORS a daily-distinct count rather than a permanent
     * identifier: the same browser hashes differently tomorrow, so nothing can be joined across
     * days even by us.
     */
    private static final int HASH_LENGTH = 32;

    private final String salt;

    public VisitorContextService(@Value("${abofonsa.privacy.hash-salt:}") String salt) {
        this.salt = salt;
    }

    /** Stable within a UTC day for a given client, unlinkable across days. */
    public String sessionHash(HttpServletRequest request) {
        return hash("session", clientIp(request) + '|' + header(request, "User-Agent") + '|' + LocalDate.now(ZoneOffset.UTC));
    }

    /** Stable for a given client, used only for rate limiting. */
    public String ipHash(HttpServletRequest request) {
        return hash("ip", clientIp(request));
    }

    /**
     * Coarse device class from the User-Agent. Deliberately crude — this feeds a dashboard facet,
     * not a rendering decision, so a wrong guess costs nothing and a full UA-parsing dependency
     * would be a poor trade.
     */
    public DeviceType deviceType(HttpServletRequest request) {
        String ua = header(request, "User-Agent");
        if (ua == null || ua.isBlank()) {
            return DeviceType.UNKNOWN;
        }
        String lower = ua.toLowerCase(Locale.ROOT);
        if (lower.contains("bot") || lower.contains("crawler") || lower.contains("spider") || lower.contains("headless")) {
            return DeviceType.BOT;
        }
        if (lower.contains("ipad") || lower.contains("tablet")) {
            return DeviceType.TABLET;
        }
        if (lower.contains("mobi") || lower.contains("android") || lower.contains("iphone")) {
            return DeviceType.MOBILE;
        }
        return DeviceType.DESKTOP;
    }

    /**
     * Host only, never the full referring URL. A referrer path can carry a search query or a
     * private document title, none of which this page has any business storing.
     */
    public String referrerHost(HttpServletRequest request) {
        String referrer = header(request, "Referer");
        if (referrer == null || referrer.isBlank()) {
            return null;
        }
        try {
            String host = URI.create(referrer).getHost();
            return host == null || host.length() > 255 ? null : host;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String userAgent(HttpServletRequest request) {
        String ua = header(request, "User-Agent");
        return ua == null ? null : ua.substring(0, Math.min(ua.length(), 512));
    }

    /**
     * Honours {@code X-Forwarded-For}'s first entry, because in every deployment of this app the
     * only thing the socket address identifies is the reverse proxy.
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = header(request, "X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        String remote = request.getRemoteAddr();
        return remote == null ? "unknown" : remote;
    }

    private String header(HttpServletRequest request, String name) {
        return request == null ? null : request.getHeader(name);
    }

    private String hash(String domain, String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest((salt + '|' + domain + '|' + value).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out).substring(0, HASH_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the JLS for every conforming JVM; this cannot happen.
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
