package com.teatro.theater.infrastructure.client;

import com.teatro.theater.adapters.input.dto.TheaterSyncRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TheaterCapacityClient {
    private final RestClient restClient;

    public TheaterCapacityClient(@Value("${api.event.url:http://localhost:8082}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void theaterCapacitySync(Long theaterId, Integer capacity) {
        TheaterSyncRequest request = new TheaterSyncRequest(theaterId, capacity);
        try {
            restClient.post()
                    .uri("api/event/internal/theater/sync/capacity")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao integrar com reservation-service para criar assetons", e);
        }
    }
}
