package com.flight.webflux.service;

import com.flight.webflux.request.BookingRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BookingService {

    Mono<Map<String, Object>> bookTicket(String flightId, BookingRequest req);

    Mono<Map<String, Object>> getTicketByPnr(String pnr);

    Mono<Map<String, Object>> getBookingHistory(String email);

    Mono<Map<String, Object>> cancelBooking(String pnr);
}

