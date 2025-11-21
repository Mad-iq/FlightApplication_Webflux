package com.flight.webflux.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.flight.webflux.entity.Flight;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightRepository extends R2dbcRepository<Flight, Long> {

    Flux<Flight> findBySourceAndDestinationAndStartDate(
            String source,
            String destination,
            LocalDateTime startDate
    );

    Flux<Flight> findBySourceAndDestinationAndStartDateBetween(
            String source,
            String destination,
            LocalDateTime start,
            LocalDateTime end
    );

    Mono<Flight> findByFlightId(String flightId);
}

