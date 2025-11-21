package com.flight.webflux.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFlightRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @Min(value = 1, message = "Number of passengers must be at least 1")
    private int numberOfPassengers;

    private boolean roundTrip;

    private LocalDate returnDate;
}
