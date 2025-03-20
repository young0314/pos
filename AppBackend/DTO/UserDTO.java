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
    private String password;
    // 회원가입 시 User 객체로 변환하는 메서드
    public User toUser() {

        return User.builder()
                .adminId(generateUniqueAdminId())  // 자동 생성된 adminId
                .adminName(this.adminName)
                .idNumber(this.idNumber)
                .phone(this.phone)
                .build();
    }

    // adminId 자동 생성 메서드
    private String generateUniqueAdminId() {
        int length = 5;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder adminIdBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            adminIdBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return adminIdBuilder.toString();
    }
}
