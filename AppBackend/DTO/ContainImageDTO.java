package com.example.pos_app.DTO;

import com.example.pos_app.Model.ContainImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainImageDTO {
    private String containNumber;
    private String containImage;

    // 엔티티 -> DTO 변환
    public static ContainImageDTO fromEntity(ContainImage containImage) {
        return ContainImageDTO.builder()
                .containNumber(containImage.getContainer().getContainNumber()) // Container 객체를 통해 containNumber 가져오기
                .containImage(containImage.getContainImage())
                .build();
    }
}
