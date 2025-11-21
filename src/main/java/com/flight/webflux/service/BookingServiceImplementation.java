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

                  
                    flight.setAvailableSeats(flight.getAvailableSeats() - req.getNumberOfSeats());

                    return flightRepo.save(flight)
                            .then(bookingRepo.save(booking))
                            .flatMap(savedBooking -> {

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

    @Override
    public Mono<Map<String, Object>> getTicketByPnr(String pnr) {
        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new RuntimeException("Booking not found")))
                .flatMap(booking ->
                        flightRepo.findById(booking.getFlightId())
                                .flatMap(flight ->
                                        passengerRepo.findAll()
                                                .filter(p -> p.getBookingId().equals(booking.getId()))
                                                .collectList()
                                                .map(passengers -> {

                                                    Map<String, Object> resp = new LinkedHashMap<>();
                                                    resp.put("pnr", booking.getPnr());
                                                    resp.put("email", booking.getEmail());
                                                    resp.put("name", booking.getName());
                                                    resp.put("source", flight.getSource());
                                                    resp.put("destination", flight.getDestination());
                                                    resp.put("journeyDateTime", booking.getTimeOfJourney());
                                                    resp.put("mealPreference", booking.getMealPreference());

                                                    List<Map<String, Object>> pList = new ArrayList<>();
                                                    for (Passenger p : passengers) {
                                                        Map<String, Object> pm = new HashMap<>();
                                                        pm.put("name", p.getName());
                                                        pm.put("gender", p.getGender());
                                                        pm.put("age", p.getAge());
                                                        pm.put("seatNumber", p.getSeatNumber());
                                                        pList.add(pm);
                                                    }
                                                    resp.put("passengers", pList);

                                                    return resp;
                                                })
                                )
                );
    }

    @Override
    public Mono<Map<String, Object>> getBookingHistory(String email) {
        return bookingRepo.findByEmail(email)
                .flatMap(b -> flightRepo.findById(b.getFlightId())
                        .map(f -> {
                            Map<String, Object> item = new LinkedHashMap<>();
                            item.put("pnr", b.getPnr());
                            item.put("date", b.getTimeOfJourney().toLocalDate().toString());
                            item.put("status", b.isCancelled() ? "CANCELLED" : "BOOKED");
                            item.put("flightId", f.getFlightId());
                            return item;
                        }))
                .collectList()
                .map(list -> {
                    Map<String, Object> resp = new LinkedHashMap<>();
                    resp.put("email", email);
                    resp.put("history", list);
                    return resp;
                });
    }

    @Override
    public Mono<Map<String, Object>> cancelBooking(String pnr) {

        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new RuntimeException("Booking not found")))
                .flatMap(booking -> {

                    if (booking.isCancelled()) {
                        return Mono.error(new RuntimeException("Booking already cancelled"));
                    }

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime deadline = booking.getTimeOfJourney().minusHours(24);

                    if (!now.isBefore(deadline)) {
                        return Mono.error(new RuntimeException("Cannot cancel within 24 hours of journey"));
                    }

                    booking.setCancelled(true);

                    return bookingRepo.save(booking)
                            .then(flightRepo.findById(booking.getFlightId()))
                            .flatMap(flight -> {
                                flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfSeats());
                                return flightRepo.save(flight);
                            })
                            .then(Mono.fromSupplier(() -> {
                                Map<String, Object> resp = new LinkedHashMap<>();
                                resp.put("pnr", pnr);
                                resp.put("message", "Booking cancelled successfully");
                                return resp;
                            }));
                });
    }
}
