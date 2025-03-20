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
public class ContainImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 새로운 id를 기본키로 사용
    private Long imageId; // 인조키 추가

    private String containImage;

    @ManyToOne(fetch = FetchType.LAZY) // 변경
    @JoinColumn(name = "containNumber") // 외래키로 containNumber 사용
    private Container container;

    public void setContainImage(String filePath) {
    }
}