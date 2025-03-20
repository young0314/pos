package com.example.pos_app.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RegiContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 새로운 id를 기본키로 사용
    private Long regiConid; // 인조키 추가

    private String containNumber; // 외래키로 사용할 containNumber 추가
    private String destination;
    private String cargo;
    private String containerOwner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "containNumber", referencedColumnName = "containNumber", insertable = false, updatable = false) // 외래키 설정
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private User user;
}
