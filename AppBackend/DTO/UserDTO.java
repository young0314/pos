package com.example.pos_app.DTO;

import com.example.pos_app.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String adminName;
    private String idNumber;
    private String phone;
    private String email;
    private String password;
    private String emailVerified;
    // 회원가입 시 User 객체로 변환하는 메서드
    public User toUser() {

        return User.builder()
                .email(this.email)
                .emailVerified(true)
                .adminName(this.adminName)
                .idNumber(this.idNumber)
                .phone(this.phone)
                .build();
    }

}
