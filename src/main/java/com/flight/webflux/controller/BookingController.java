package com.flight.webflux.controller;

import com.flight.webflux.request.BookingRequest;
import com.flight.webflux.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<?>> bookTicket(
            @PathVariable String flightId,
            @Valid @RequestBody BookingRequest req) {

        return bookingService.bookTicket(flightId, req)
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp));
    }

    @GetMapping("/ticket/{pnr}")
    public Mono<ResponseEntity<?>> getTicket(@PathVariable String pnr) {
        return bookingService.getTicketByPnr(pnr)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/booking/history/{email}")
    public Mono<ResponseEntity<?>> getHistory(@PathVariable String email) {
        return bookingService.getBookingHistory(email)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ResponseEntity<?>> cancel(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .map(ResponseEntity::ok);
    }
}
