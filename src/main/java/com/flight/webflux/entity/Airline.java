package com.flight.webflux.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("airline")
public class Airline {

    @Id
    private Long id;

    private String name;
    
    public Airline(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Airline() {}
}
