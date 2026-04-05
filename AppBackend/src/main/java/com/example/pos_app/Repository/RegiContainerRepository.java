package com.example.pos_app.Repository;

import com.example.pos_app.Model.RegiContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegiContainerRepository extends JpaRepository<RegiContainer, Long> {

    Optional<RegiContainer> findByContainNumber(String containNumber);
    Optional<RegiContainer> findByDeviceId(String deviceId);


    boolean existsByContainNumber(String containNumber);

    List<RegiContainer> findByUser_AdPk(Long adPk);

    boolean existsByDeviceId(String deviceId);
}