package com.example.pos_app.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RegiContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regiConid;

    @Column(unique = true, nullable = false)
    private String containNumber; //컨테이너 번호

    @Column(nullable = false, unique = true)
    private String deviceId; //라즈베리파이

    private String destination;
    private String cargo;
    private String containerOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adPk")
    private User user;

}