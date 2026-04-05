package com.example.pos_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertMessageDTO {
    private String type;
    private String message;
    private String containNumber;
    private String deviceId;
    private Long adPk;
}