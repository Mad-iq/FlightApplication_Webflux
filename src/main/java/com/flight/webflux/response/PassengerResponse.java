package com.flight.webflux.response;

import lombok.Data;

@Data
public class PassengerResponse {

    private String name;
    private String gender;
    private int age;
    private String seatNumber;
}

