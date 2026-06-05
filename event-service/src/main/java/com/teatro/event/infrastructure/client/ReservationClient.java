package com.teatro.event.infrastructure.client;

import com.teatro.event.adapters.input.dto.InitializeSeatsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ReservationClient {
    private final RestClient restClient;

    public ReservationClient(@Value("${api.reservation.url:http://localhost:8083}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void initializeSeats(Long eventId, int totalSeats) {
        InitializeSeatsRequest request = new InitializeSeatsRequest(eventId, totalSeats);
        try {
            restClient.post()
                    .uri("api/reservation/internal/seats/initialize")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("Chamada efetuada!");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao integrar com reservation-service para criar assetons", e);
        }
    }
}
