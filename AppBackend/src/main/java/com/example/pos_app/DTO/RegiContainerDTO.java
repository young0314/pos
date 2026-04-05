package com.example.pos_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegiContainerDTO {
    private String containNumber;
    private String deviceId;
    private String destination;
    private String cargo;
    private String containerOwner;
}



