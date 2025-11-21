package com.flight.webflux.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {

    private String pnr;
    private String email;
    private String name;

    private LocalDateTime timeOfBooking;
    private LocalDateTime timeOfJourney;

    private int numberOfSeats;
    private double totalPrice;
    private String mealPreference;
    private boolean cancelled;

    private String flightId;
    private String source;
    private String destination;

    private List<PassengerResponse> passengers;
}
