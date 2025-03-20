package com.example.pos_app.Repository;

import com.example.pos_app.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByIdNumber(String idNumber); // idNumber로 사용자 조회
    Optional<User> findByAdminNameAndIdNumber(String adminName, String idNumber); //adminName이랑 idNumber로 관리자번호 조회
    Optional<User> findByAdminIdAndIdNumber(String adminId, String idNumber);
    Optional<User> findByAdminId(String adminId);

}
