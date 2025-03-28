package com.example.pos_app.Repository;

import com.example.pos_app.Model.RegiContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RegiContainerRepository extends JpaRepository<RegiContainer, Long>{
    List<RegiContainer> findByUser_AdminId(String adminId);  // adminId로 RegiContainer 조회

    // containNumber로 RegiContainer 찾기
    List<RegiContainer> findByContainNumber(String containNumber);

}
