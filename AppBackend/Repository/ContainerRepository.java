package com.example.pos_app.Repository;

import com.example.pos_app.Model.Container;
import com.example.pos_app.Model.RegiContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  ContainerRepository extends JpaRepository<Container, String> {
    Optional<Container> findByContainNumber(String containNumber); // 이 메서드는 Container 엔티티에서 containNumber를 기준으로 조회

    // 필요한 메서드를 추가할 수 있음 (예: findByContainNumber 등)
}
