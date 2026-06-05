package com.teatro.reservation.adapters.input;

import com.teatro.reservation.adapters.input.dto.InitializeSeatsRequest;
import com.teatro.reservation.adapters.input.dto.ReservationRequest;
import com.teatro.reservation.adapters.input.dto.ReservationResponse;
import com.teatro.reservation.adapters.input.dto.ReservedSeatResponse;
import com.teatro.reservation.ports.input.BookingUseCase;
import com.teatro.reservation.ports.input.FindAllUserReservedSeatsUseCase;
import com.teatro.reservation.ports.input.FindUserReservedSeatsUseCase;
import com.teatro.reservation.ports.input.InitializeSeatsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final BookingUseCase bookingUseCase;
    private final InitializeSeatsUseCase initializeSeatsUseCase;
    private final FindUserReservedSeatsUseCase findUserReservedSeatsUseCase;
    private final FindAllUserReservedSeatsUseCase findAllUserReservedSeatsUseCase;

    public ReservationController(BookingUseCase bookingUseCase,
                                 InitializeSeatsUseCase initializeSeatsUseCase,
                                 FindUserReservedSeatsUseCase findUserReservedSeatsUseCase,
                                 FindAllUserReservedSeatsUseCase findAllUserReservedSeatsUseCase) {
        this.bookingUseCase = bookingUseCase;
        this.initializeSeatsUseCase = initializeSeatsUseCase;
        this.findUserReservedSeatsUseCase = findUserReservedSeatsUseCase;
        this.findAllUserReservedSeatsUseCase = findAllUserReservedSeatsUseCase;
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

    @GetMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<List<ReservedSeatResponse>> getUserReservedSeats(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        List<ReservedSeatResponse> response = findUserReservedSeatsUseCase.execute(eventId, userId).stream()
                .map(seat -> new ReservedSeatResponse(seat.getId(), seat.getSeatCode(), seat.getEventId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservedSeatResponse>> getAllUserReservedSeats(
            @PathVariable Long userId
    ) {
        List<ReservedSeatResponse> response = findAllUserReservedSeatsUseCase.execute(userId).stream()
                .map(seat -> new ReservedSeatResponse(seat.getId(), seat.getSeatCode(), seat.getEventId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
