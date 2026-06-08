package com.teatro.event.infrastructure.listener;

import com.teatro.event.domain.event.EventCreatedEvent;
import com.teatro.event.infrastructure.client.ReservationClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventCreatedListener {
    private final ReservationClient reservationClient;

    public EventCreatedListener(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventCreated(EventCreatedEvent event) {
        reservationClient.initializeSeats(event.eventId(), event.totalSeats());
    }
}
