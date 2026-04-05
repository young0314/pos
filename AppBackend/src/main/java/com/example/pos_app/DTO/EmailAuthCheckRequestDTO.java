package com.example.pos_app.DTO;

import lombok.Getter;

@Getter
public class EmailAuthCheckRequestDTO {
    private String email;
    private String authCode;
}
