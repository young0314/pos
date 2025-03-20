package com.example.pos_app.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Container {
    @Id
    private String containNumber;

    @CreationTimestamp
    private LocalDateTime createdDate;
    private double temperature;
    private int humidity;
    private String lifespan;
    private int doorStatus;
    private int errorStatus;
}
