package com.flight.webflux.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("passenger")
public class Passenger {

    @Id
    private Long id; 

    private String name;
    private String gender;
    private int age;
    private String seatNumber;
    private Long bookingId;
}
