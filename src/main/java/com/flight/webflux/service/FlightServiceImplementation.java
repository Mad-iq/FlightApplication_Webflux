package com.flight.webflux.service;

import com.flight.webflux.entity.Airline;
import com.flight.webflux.entity.Flight;
import com.flight.webflux.repository.AirlineRepository;
import com.flight.webflux.repository.FlightRepository;
import com.flight.webflux.request.AddFlightRequest;
import com.flight.webflux.request.SearchFlightRequest;
import com.flight.webflux.utility.FlightIDGenerator;
//import com.flight.webflux.service.FlightService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlightServiceImplementation implements FlightService {

    private final AirlineRepository airlineRepo;
    private final FlightRepository flightRepo;

    @Override
    public Mono<Map<String, Object>> addFlight(AddFlightRequest req) {

        LocalDateTime start = req.getStartDate();
        LocalDateTime end   = req.getEndDate();

        return airlineRepo.findByName(req.getAirlineName())
                .switchIfEmpty(
                        airlineRepo.save(new Airline(null, req.getAirlineName()))
                )
                .flatMap(airline ->flightRepo.findBySourceAndDestinationAndStartDate(
                                        req.getSource(),
                                        req.getDestination(),
                                        start
                                ).hasElements().flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new RuntimeException("Flight already exists"));
                                    }

                                    String flightId = FlightIDGenerator.generateFlightId(
                                            airline,
                                            req.getSource(),
                                            req.getDestination(),
                                            start
                                    );

                                    Flight flight = new Flight();
                                    flight.setFlightId(flightId);
                                    flight.setSource(req.getSource());
                                    flight.setDestination(req.getDestination());
                                    flight.setStartDate(start);
                                    flight.setEndDate(end);
                                    flight.setAvailableSeats(req.getAvailableSeats());
                                    flight.setTicketPrice(req.getTicketPrice());
                                    flight.setMealStatus(req.isMealStatus());
                                    flight.setAirlineId(airline.getId());

                                    return flightRepo.save(flight)
                                            .map(saved -> {
                                                Map<String, Object> resp = new LinkedHashMap<>();
                                                resp.put("message", "Flight added successfully");
                                                resp.put("flightId", flightId);
                                                resp.put("status", "CREATED");
                                                return resp;
                                            });
                                })
                );
    }


    @Override
    public Mono<Map<String, Object>> searchFlight(SearchFlightRequest req) {

        LocalDateTime startOfDay = req.getJourneyDate().atStartOfDay();
        LocalDateTime endOfDay   = req.getJourneyDate().atTime(23, 59);

        Flux<Flight> onward = flightRepo.findBySourceAndDestinationAndStartDateBetween(
                req.getSource(),
                req.getDestination(),
                startOfDay,
                endOfDay
        );

        Mono<Map<String, Object>> onwardMapped =
                onward.map(this::mapFlight)
                      .collectList()
                      .map(list -> {
                          Map<String, Object> resp = new LinkedHashMap<>();
                          resp.put("onwardFlights", list);
                          return resp;
                      });

        if (!req.isRoundTrip()) {
            return onwardMapped;
        }

        if (req.getReturnDate() == null) {
            return Mono.error(new RuntimeException("Return date is required"));
        }

        LocalDateTime returnStart = req.getReturnDate().atStartOfDay();
        LocalDateTime returnEnd   = req.getReturnDate().atTime(23, 59);

        Flux<Flight> returning = flightRepo.findBySourceAndDestinationAndStartDateBetween(
                req.getDestination(),
                req.getSource(),
                returnStart,
                returnEnd
        );

        Mono<List<Map<String,Object>>> returnMapped =
                returning.map(this::mapFlight).collectList();

        return onwardMapped.zipWith(returnMapped, (map, returnList) -> {
            map.put("returnFlights", returnList);
            return map;
        });
    }

    private Map<String, Object> mapFlight(Flight flight) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("flightId", flight.getFlightId());
        m.put("airlineId", flight.getAirlineId());
        m.put("dateTime", flight.getStartDate().toString());
        m.put("price", flight.getTicketPrice());
        return m;
    }
}
