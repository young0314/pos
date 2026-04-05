package com.example.pos_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerStateRequestDTO {

    private String deviceId;

    private double temperature;
    private int humidity;
    private String lifespan;
    private int doorStatus;
    private int errorStatus;
}
