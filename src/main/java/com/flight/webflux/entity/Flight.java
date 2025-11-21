package com.flight.webflux.entity;


import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("flight")
public class Flight {

    @Id
    private Long id;   

    private String flightId;

    private String source;
    private String destination;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int availableSeats;
    private double ticketPrice;

    private boolean mealStatus;

    // replace many2one with manual foreign key field
    private Long airlineId;
}
