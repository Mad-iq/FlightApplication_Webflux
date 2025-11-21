package com.flight.webflux.service;

import java.util.Map;

import com.flight.webflux.request.AddFlightRequest;
import com.flight.webflux.request.SearchFlightRequest;

import reactor.core.publisher.Mono;

public interface FlightService {

    Mono<Map<String, Object>> addFlight(AddFlightRequest req);

    Mono<Map<String, Object>> searchFlight(SearchFlightRequest req);
}
