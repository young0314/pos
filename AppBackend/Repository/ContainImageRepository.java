package com.example.pos_app.Repository;

import com.example.pos_app.Model.ContainImage;
import com.example.pos_app.Model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContainImageRepository extends JpaRepository<ContainImage, Long> {
    Optional<ContainImage> findByContainer_ContainNumber(String containNumber);
}
