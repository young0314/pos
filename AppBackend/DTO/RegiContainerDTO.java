package com.example.pos_app.DTO;

import com.example.pos_app.Model.RegiContainer;
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
    private String destination;
    private String cargo;
    private String containerOwner;
    private String adminId; // Admin의 ID만 포함

}



