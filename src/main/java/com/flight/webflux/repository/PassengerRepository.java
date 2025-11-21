package com.flight.webflux.repository;

import com.flight.webflux.entity.Passenger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PassengerRepository extends ReactiveCrudRepository<Passenger, Long> {}
