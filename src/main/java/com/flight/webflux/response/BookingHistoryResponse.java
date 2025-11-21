package com.flight.webflux.response;

import lombok.Data;

import java.util.List;

@Data
public class BookingHistoryResponse {

    private String email;
    private List<BookingResponse> bookings;
}
