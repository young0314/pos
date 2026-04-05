package com.example.pos_app.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adPk; //PK
    @Column(unique = true, nullable = false)
    private String email; //아이디로 사용
    private boolean emailVerified; //이메일 인증 여부
    private String idNumber; //주민번호
    private String phone;
    private String adminName; //이름
    private String password; //비밀번호

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @CreationTimestamp
    private LocalDateTime createdDate;

}
