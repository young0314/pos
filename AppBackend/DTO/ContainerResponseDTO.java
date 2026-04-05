package com.example.pos_app.DTO;

import com.example.pos_app.Model.Container;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerResponseDTO {
    private String containNumber;
    private double temperature;
    private int humidity;
    private String lifespan;
    private int doorStatus;
    private int errorStatus;

    // 엔티티 -> DTO 변환
    public static ContainerResponseDTO fromEntity(Container container) {
        return ContainerResponseDTO.builder()
                .containNumber(container.getContainNumber())
                .temperature(container.getTemperature())
                .humidity(container.getHumidity())
                .lifespan(container.getLifespan())
                .doorStatus(container.getDoorStatus())
                .errorStatus(container.getErrorStatus())
                .build();
    }
}
