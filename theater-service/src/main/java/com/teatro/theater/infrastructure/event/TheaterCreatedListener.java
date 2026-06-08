package com.teatro.theater.infrastructure.event;

import com.teatro.theater.domain.event.EventTheaterCreated;
import com.teatro.theater.infrastructure.client.TheaterCapacityClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TheaterCreatedListener {
    private final TheaterCapacityClient theaterCapacityClient;

    public TheaterCreatedListener(TheaterCapacityClient theaterCapacityClient) {
        this.theaterCapacityClient = theaterCapacityClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventCreated(EventTheaterCreated theater) {
        theaterCapacityClient.theaterCapacitySync(theater.id(), theater.capacity());
    }
}
