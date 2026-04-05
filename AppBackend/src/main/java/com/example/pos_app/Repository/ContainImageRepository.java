package com.example.pos_app.Repository;

import com.example.pos_app.Model.ContainImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContainImageRepository extends JpaRepository<ContainImage, Long> {
    Optional<ContainImage> findByContainer_ContainNumber(String containNumber);
    List<ContainImage> findAllByContainer_ContainNumber(String containNumber);

}
