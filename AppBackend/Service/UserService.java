package com.example.pos_app.Service;

import com.example.pos_app.DTO.UserDTO;
import com.example.pos_app.Model.User;
import com.example.pos_app.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder 사용

    // 회원가입 시 idNumber 중복 확인 로직 추가
    public User registerAdmin(UserDTO userDto) {
        // idNumber 중복 여부 확인
        Optional<User> existingUser = userRepository.findByIdNumber(userDto.getIdNumber());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다."); // 중복된 사용자가 있을 경우 예외 던짐
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());

        // UserDTO에서 User 객체로 변환하고, 암호화된 비밀번호를 설정
        User user = userDto.toUser(); // adminId는 자동 생성
        user = user.toBuilder()
                .password(encryptedPassword) // 암호화된 비밀번호 설정
                .build();

        // 새로운 사용자 저장
        return userRepository.save(user);
    }

    //로그인기능
    public Optional<User> login(String adminId, String rawPassword) {
        Optional<User> user = userRepository.findById(adminId);

        if (user.isPresent()) {
            // 암호화된 비밀번호와 사용자가 입력한 비밀번호 비교
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }
    //관리자번호 다시 찾기 기능
    public Optional<User> findUserByAdminNameAndIdNumber(String adminName, String idNumber) {
        return userRepository.findByAdminNameAndIdNumber(adminName, idNumber);
    }

    // adminId, idNumber로 사용자 조회
    public Optional<User> findByAdminIdAndIdNumber(String adminId, String idNumber) {
        System.out.println("findByAdminIdAndIdNumber함수 실행");
        return userRepository.findByAdminIdAndIdNumber(adminId, idNumber);
    }
    // adminId로 사용자 조회
    public Optional<User> findByAdminId(String adminId) {
        return userRepository.findByAdminId(adminId);
    }
    //비밀번호 재설정
    public void updatePassword(User user, String newPassword) {
        // 새 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(newPassword);

        // 사용자 객체의 비밀번호 업데이트 및 저장
        user.setPassword(encryptedPassword);
        System.out.println("새비밀번호 업데이트 완료");
        userRepository.save(user);
    }

    // adminId로 사용자 정보 조회
    public User getUserByAdminId(String adminId) {
        return userRepository.findById(adminId).orElseThrow(() -> new RuntimeException("User not found"));
    }


}
