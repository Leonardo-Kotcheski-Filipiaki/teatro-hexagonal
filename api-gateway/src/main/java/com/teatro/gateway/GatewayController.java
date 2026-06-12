package com.teatro.gateway;

import com.teatro.shared.infrastructure.security.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;

@RestController
public class GatewayController {

    @Value("${services.auth.url:http://localhost:8084}")
    private String authServiceUrl;

    @Value("${services.theater.url:http://localhost:8081}")
    private String theaterServiceUrl;

    @Value("${services.event.url:http://localhost:8082}")
    private String eventServiceUrl;

    @Value("${services.reservation.url:http://localhost:8083}")
    private String reservationServiceUrl;

    private final JwtValidator jwtValidator;
    private final RestClient restClient = RestClient.create();

    private static final Set<String> IGNORED_HEADERS = Set.of(
            "host", "content-length", "connection", "accept-encoding"
    );

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register/customer",
            "/api/event/internal/theater/sync/capacity",
            "/api/reservation/internal/seats/initialize"
    );

    public GatewayController(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    private boolean isPublicPath(String path) {
        String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return PUBLIC_PATHS.contains(normalized);
    }

    @RequestMapping("/api/{service}/**")
    public ResponseEntity<byte[]> proxy(
            @PathVariable("service") String service,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body) {

        String path = request.getRequestURI();

        String email = null;
        String role  = null;

        if (!isPublicPath(path)) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return error(HttpStatus.UNAUTHORIZED, "Token ausente ou formato inválido");
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = jwtValidator.validate(token);
                email = claims.getSubject();
                role  = claims.get("role", String.class);
            } catch (ExpiredJwtException e) {
                return error(HttpStatus.UNAUTHORIZED, "Token expirado");
            } catch (Exception e) {
                return error(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
        }

        String targetBaseUrl = switch (service.toLowerCase()) {
            case "auth", "user" -> authServiceUrl;
            case "theater"      -> theaterServiceUrl;
            case "event"        -> eventServiceUrl;
            case "reservation"  -> reservationServiceUrl;
            default             -> null;
        };

        if (targetBaseUrl == null) {
            return error(HttpStatus.NOT_FOUND, "Service '" + service + "' not found");
        }

        String query     = request.getQueryString();
        String targetUrl = targetBaseUrl + path + (query != null ? "?" + query : "");

        RestClient.RequestBodySpec spec = restClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(targetUrl);

        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (!IGNORED_HEADERS.contains(name.toLowerCase())) {
                    spec.header(name, request.getHeader(name));
                }
            }
        }

        if (email != null) spec.header("X-User-Email", email);
        if (role  != null) spec.header("X-User-Role",  role);

        if (body != null && body.length > 0) {
            spec.body(body);
        }

        try {
            return spec.exchange((req, res) -> {
                byte[] responseBody = res.getBody().readAllBytes();
                ResponseEntity.BodyBuilder builder = ResponseEntity.status(res.getStatusCode());
                res.getHeaders().forEach((name, values) -> {
                    if (!name.equalsIgnoreCase("transfer-encoding") &&
                        !name.equalsIgnoreCase("content-length")) {
                        values.forEach(v -> builder.header(name, v));
                    }
                });
                return builder.body(responseBody);
            }, false);
        } catch (Exception e) {
            return error(HttpStatus.BAD_GATEWAY, "Gateway Error: " + e.getMessage());
        }
    }

    private ResponseEntity<byte[]> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(("{\"error\": \"" + message + "\"}").getBytes(StandardCharsets.UTF_8));
    }
}
