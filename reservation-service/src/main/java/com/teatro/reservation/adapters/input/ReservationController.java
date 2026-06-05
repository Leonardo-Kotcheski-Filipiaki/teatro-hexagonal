package com.teatro.reservation.adapters.input;

import com.teatro.reservation.adapters.input.dto.InitializeSeatsRequest;
import com.teatro.reservation.adapters.input.dto.ReservationRequest;
import com.teatro.reservation.adapters.input.dto.ReservationResponse;
import com.teatro.reservation.ports.input.BookingUseCase;
import com.teatro.reservation.ports.input.InitializeSeatsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final BookingUseCase bookingUseCase;

    private final InitializeSeatsUseCase initializeSeatsUseCase;

    public ReservationController(BookingUseCase bookingUseCase, InitializeSeatsUseCase initializeSeatsUseCase) {
        this.bookingUseCase = bookingUseCase;
        this.initializeSeatsUseCase = initializeSeatsUseCase;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReservationRequest request) {
        bookingUseCase.execute(
                request.eventId(),
                request.userId(),
                request.seatIds()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ReservationResponse("Reserva(s) realizada e confirmada com sucesso!"));
    }

    @PostMapping("/internal/seats/initialize")
    public ResponseEntity<Void> initializeSeats(@RequestBody InitializeSeatsRequest request) {
        initializeSeatsUseCase.execute(request.eventId(), request.totalSeats());

        return ResponseEntity.ok().build();
    }

}
