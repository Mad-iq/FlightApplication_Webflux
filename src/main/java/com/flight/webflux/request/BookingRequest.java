package com.flight.webflux.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 1, message = "At least 1 seat must be booked")
    private int numberOfSeats;

    @NotEmpty(message = "Passenger list cannot be empty")
    private List<PassengerRequest> passengers;

    @NotBlank(message = "Meal preference is required")
    private String mealPreference;

    @NotEmpty(message = "Seat numbers cannot be empty")
    private List<String> seatNumbers;
}
