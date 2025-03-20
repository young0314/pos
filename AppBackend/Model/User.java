package com.example.pos_app.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class User {
    @Id
    private String adminId;
    private String adminName;
    private String idNumber;
    private String phone;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdDate;

}