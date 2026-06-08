package com.teatro.reservation.adapters.input;

import com.teatro.reservation.adapters.input.dto.*;
import com.teatro.reservation.ports.input.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final FindAllSeatsByEventIdUseCase findAllSeatsByEventIdUseCase;

    public ReservationController(BookingUseCase bookingUseCase,
                                 InitializeSeatsUseCase initializeSeatsUseCase,
                                 FindUserReservedSeatsUseCase findUserReservedSeatsUseCase,
                                 FindAllUserReservedSeatsUseCase findAllUserReservedSeatsUseCase,
                                 FindAllSeatsByEventIdUseCase findAllSeatsByEventIdUseCase) {
        this.bookingUseCase = bookingUseCase;
        this.initializeSeatsUseCase = initializeSeatsUseCase;
        this.findUserReservedSeatsUseCase = findUserReservedSeatsUseCase;
        this.findAllUserReservedSeatsUseCase = findAllUserReservedSeatsUseCase;
        this.findAllSeatsByEventIdUseCase = findAllSeatsByEventIdUseCase;
    }

    @PostMapping("/reserve")
    @Transactional
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

    @GetMapping("/event/seats/{eventId}")
    public ResponseEntity<List<SeatResponse>> getAllSeatsByEventId(
            @PathVariable Long eventId
            ) {
        List<SeatResponse> response = findAllSeatsByEventIdUseCase.execute(eventId)
                .stream().map(seat -> new SeatResponse(seat.getId(), seat.getSeatCode(), seat.getStatus(), seat.getReservedAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
