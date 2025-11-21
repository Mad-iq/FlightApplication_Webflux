package com.flight.webflux.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightResponse {

    private String flightId;
    private String source;
    private String destination;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int availableSeats;
    private double ticketPrice;
    private boolean mealStatus;

    private String airlineName;
}
