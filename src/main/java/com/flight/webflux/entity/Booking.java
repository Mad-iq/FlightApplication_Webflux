package com.flight.webflux.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("booking")
public class Booking {

    @Id
    private Long id;

    private String pnr;

    private String email;
    private String name;

    private LocalDateTime timeOfBooking;
    private LocalDateTime timeOfJourney;

    private int numberOfSeats;
    private double totalPrice;

    private String mealPreference;

    private boolean cancelled;
    //manual fk
    private Long flightId;
}
