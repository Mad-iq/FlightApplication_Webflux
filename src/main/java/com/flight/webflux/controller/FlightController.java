package com.flight.webflux.controller;


import com.flight.webflux.request.AddFlightRequest;
import com.flight.webflux.request.SearchFlightRequest;
import com.flight.webflux.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/add")
    public Mono<ResponseEntity<?>> addFlight(@Valid @RequestBody AddFlightRequest request) {
        return flightService.addFlight(request)
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp));
    }

    @PostMapping("/search")
    public Mono<ResponseEntity<?>> search(@Valid @RequestBody SearchFlightRequest request) {
        return flightService.searchFlight(request)
                .map(ResponseEntity::ok);
    }
}

