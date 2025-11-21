package com.flight.webflux.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.flight.webflux.entity.Airline;

import reactor.core.publisher.Mono;

public interface AirlineRepository extends R2dbcRepository<Airline, Long> {

    Mono<Airline> findByName(String name);
}
