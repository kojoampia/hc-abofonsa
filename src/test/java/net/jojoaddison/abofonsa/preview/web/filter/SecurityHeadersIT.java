package net.jojoaddison.abofonsa.preview.web.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import net.jojoaddison.abofonsa.preview.WebServerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Security headers must reach the HTML document, not only the JSON API.
 *
 * <p>They did not. Every SPA route is answered by {@link SpaWebFilter} forwarding to
 * {@code /index.html}, and a forward does not re-enter the security filter chain, so the header
 * writers never ran against the response the container committed. Live, that meant {@code GET /} and
 * {@code GET /login} returned no Content-Security-Policy, no X-Frame-Options, no Referrer-Policy and
 * no nosniff, while {@code GET /api/public/content} returned all four. A CSP delivered on a JSON
 * response protects nothing — it governs the document that loads the scripts.
 *
 * <p>A full-container test on a real port, rather than a MockMvc one. MockMvc records a forward as an
 * expectation and never performs it, so it cannot observe what the container actually writes — which
 * is why the existing {@link SpaWebFilterIT} was blind to this, and why a replacement written the
 * same way would be blind again. The JDK's own HTTP client is used because Spring Boot 4 no longer
 * ships {@code TestRestTemplate} in {@code spring-boot-test}, and it does not follow redirects by
 * default, which is what we want here.
 */
@WebServerIntegrationTest
class SecurityHeadersIT {

    @LocalServerPort
    private int port;

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String header(HttpResponse<String> response, String name) {
        return response.headers().firstValue(name).orElse(null);
    }

    /** The SPA routes a visitor actually lands on, plus the root. */
    @ParameterizedTest
    @ValueSource(strings = { "/", "/login", "/confirm", "/some/deep/route" })
    void htmlDocumentsCarrySecurityHeaders(String path) throws Exception {
        HttpResponse<String> response = get(path);

        // A 200 matters: it proves the forward resolved, and therefore that these headers came from
        // the forwarded document rather than from an error page. The ERROR dispatch was always
        // covered, so asserting on a 404 would have passed against the broken configuration too.
        assertThat(response.statusCode()).as("status for %s", path).isEqualTo(200);

        assertThat(header(response, "Content-Security-Policy"))
            .as("CSP on %s", path)
            .isNotNull()
            .contains("default-src 'self'")
            .contains("frame-ancestors 'none'")
            // Two of the three the generator's stock policy handed out and this one does not.
            .doesNotContain("unsafe-eval")
            .doesNotContain("storage.googleapis.com");

        assertThat(header(response, "X-Content-Type-Options")).as("nosniff on %s", path).isEqualTo("nosniff");
        assertThat(header(response, "X-Frame-Options")).as("frame options on %s", path).isEqualTo("SAMEORIGIN");
        assertThat(header(response, "Referrer-Policy")).as("referrer policy on %s", path).isEqualTo("strict-origin-when-cross-origin");
    }

    /** The API kept its headers throughout; this guards against fixing one and losing the other. */
    @Test
    void theApiStillCarriesSecurityHeaders() throws Exception {
        HttpResponse<String> response = get("/api/public/content");
        assertThat(header(response, "Content-Security-Policy")).contains("default-src 'self'");
        assertThat(header(response, "X-Content-Type-Options")).isEqualTo("nosniff");
    }

    /**
     * HSTS is absent here and that is correct: the test client speaks plain HTTP, and Spring Security
     * withholds the header on a request it does not consider secure. In production it appears because
     * nginx sends X-Forwarded-Proto and {@code server.forward-headers-strategy: framework} makes
     * Spring believe it — the part that was missing, and the part this test cannot reach.
     */
    @Test
    void hstsIsWithheldOverPlainHttp() throws Exception {
        assertThat(header(get("/"), "Strict-Transport-Security")).isNull();
    }
}
