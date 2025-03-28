package com.example.pos_app.Repository;

import com.example.pos_app.Model.Container;
import com.example.pos_app.Model.RegiContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  ContainerRepository extends JpaRepository<Container, String> {
    Optional<Container> findByContainNumber(String containNumber); 

}
