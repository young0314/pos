package com.example.pos_app.Service;

import com.example.pos_app.Config.RedisUtil;
import com.example.pos_app.DTO.UserDTO;
import com.example.pos_app.Model.User;
import com.example.pos_app.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RedisUtil redisUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
    }

    // 회원가입
    public User registerAdmin(UserDTO userDto) {

        // 1. 이메일 인증 여부 확인 추가
        String verified = redisUtil.getData("verified:" + userDto.getEmail());
        if (verified == null) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 2. idNumber 중복 여부 확인
        Optional<User> existingUser = userRepository.findByIdNumber(userDto.getIdNumber());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 3. 이메일 중복 체크
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 4. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());

        // 5. UserDTO -> User 변환 후 값 세팅
        User user = userDto.toUser();
        user = user.toBuilder()
                .password(encryptedPassword)
                .emailVerified(true)
                .build();

        // 6. 회원가입 완료 후 인증 완료 키 삭제
        redisUtil.deleteData("verified:" + userDto.getEmail());

        // 7. 저장
        return userRepository.save(user);
    }

    // 로그인기능
    public Optional<User> login(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    // 이메일 찾기 기능
    public Optional<User> findUserByAdminNameAndIdNumber(String adminName, String idNumber) {
        return userRepository.findByAdminNameAndIdNumber(adminName, idNumber);
    }

    // PK 조회
    public Optional<User> findById(Long adPk) {
        return userRepository.findById(adPk);
    }

    // email, idNumber로 사용자 조회
    public Optional<User> findByEmailAndIdNumber(String email, String idNumber) {
        System.out.println("findByAdminIdAndIdNumber함수 실행");
        return userRepository.findByEmailAndIdNumber(email, idNumber);
    }

    // email 중복 확인
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // email로 사용자 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 비밀번호 재설정
    public void updatePassword(User user, String newPassword) {
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        System.out.println("새비밀번호 업데이트 완료");
        userRepository.save(user);
    }
}