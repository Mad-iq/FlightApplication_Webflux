package com.flight.webflux.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.flight.webflux.entity.Booking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends R2dbcRepository<Booking, Long> {

    Mono<Booking> findByPnr(String pnr);

    Flux<Booking> findByEmail(String email);
}

