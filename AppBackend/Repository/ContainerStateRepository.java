package com.example.pos_app.Repository;

import com.example.pos_app.Model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerStateRepository extends JpaRepository<Container, String> {
    // container를 기준으로 조회
    boolean existsByContainNumber(String containNumber);
}