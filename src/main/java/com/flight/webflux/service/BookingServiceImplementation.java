package com.flight.webflux.service;

import com.flight.webflux.entity.Booking;
import com.flight.webflux.entity.Flight;
import com.flight.webflux.entity.Passenger;
import com.flight.webflux.repository.BookingRepository;
import com.flight.webflux.repository.FlightRepository;
import com.flight.webflux.repository.PassengerRepository;
import com.flight.webflux.request.BookingRequest;
import com.flight.webflux.request.PassengerRequest;
import com.flight.webflux.service.BookingService;
import com.flight.webflux.utility.FlightIDGenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImplementation implements BookingService {

    private final FlightRepository flightRepo;
    private final BookingRepository bookingRepo;
    private final PassengerRepository passengerRepo;

    @Override
    public Mono<Map<String, Object>> bookTicket(String flightId, BookingRequest req) {

        return flightRepo.findByFlightId(flightId)
                .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
                .flatMap(flight -> {

                    if (flight.getAvailableSeats() < req.getNumberOfSeats()) {
                        return Mono.error(new RuntimeException("Not enough seats available"));
                    }

                    
                    Booking booking = new Booking();
                    booking.setPnr(FlightIDGenerator.generatePnr());
                    booking.setEmail(req.getEmail());
                    booking.setName(req.getName());
                    booking.setTimeOfBooking(LocalDateTime.now());
                    booking.setTimeOfJourney(flight.getStartDate());
                    booking.setNumberOfSeats(req.getNumberOfSeats());
                    booking.setTotalPrice(req.getNumberOfSeats() * flight.getTicketPrice());
                    booking.setMealPreference(req.getMealPreference());
                    booking.setCancelled(false);
                    booking.setFlightId(flight.getId());

                    // Reduce flight seats
                    flight.setAvailableSeats(flight.getAvailableSeats() - req.getNumberOfSeats());

                    return flightRepo.save(flight)
                            .then(bookingRepo.save(booking))
                            .flatMap(savedBooking -> {

                                // Convert passengers DTO → passenger entities
                                List<Passenger> pList = new ArrayList<>();

                                for (int i = 0; i < req.getPassengers().size(); i++) {
                                    PassengerRequest pr = req.getPassengers().get(i);

                                    Passenger p = new Passenger();
                                    p.setName(pr.getName());
                                    p.setGender(pr.getGender());
                                    p.setAge(pr.getAge());
                                    p.setSeatNumber(req.getSeatNumbers().get(i));
                                    p.setBookingId(savedBooking.getId());

                                    pList.add(p);
                                }

                                return passengerRepo.saveAll(pList).collectList()
                                        .map(pass -> {
                                            Map<String, Object> resp = new LinkedHashMap<>();
                                            resp.put("message", "Booking successful");
                                            resp.put("pnr", savedBooking.getPnr());
                                            resp.put("totalPrice", savedBooking.getTotalPrice());
                                            resp.put("flightId", flight.getFlightId());
                                            return resp;
                                        });
                            });
                });
    }

   
}

