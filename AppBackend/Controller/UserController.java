package com.example.pos_app.Controller;

import com.example.pos_app.Model.User;
import com.example.pos_app.Model.SessionManager;
import com.example.pos_app.DTO.UserDTO;
import com.example.pos_app.DTO.UserLoginDTO;
import com.example.pos_app.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class UserController {


    private final UserService userService;
    private final SessionManager sessionManager;

    public UserController(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    // 회원가입 메서드 (POST 요청)
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody UserDTO userDto) {
        // 서비스 레이어에서 회원가입 처리
        User newUser = userService.registerAdmin(userDto);

        // 응답 데이터를 구성
        Map<String, Object> response = new HashMap<>();
        //response.put("user", newUser);

        response.put("email", newUser.getEmail());
        response.put("success", true);
        response.put("adminName", newUser.getAdminName());
        response.put("idNumber", newUser.getIdNumber());
        response.put("phone", newUser.getPhone());
        response.put("createdDate", newUser.getCreatedDate());
        System.out.println("회원가입 성공");
        // 생성된 adminId 출력
        // JSON 데이터와 함께 201 Created 상태 코드 반환
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    //이메일 중복 확인
    @PostMapping("/email/check")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이메일을 입력해주세요."
            ));
        }

        email = email.trim();
        boolean exists = userService.existsByEmail(email);

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", "이미 사용 중인 이메일입니다."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용 가능한 이메일입니다."
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserLoginDTO loginDto, HttpServletRequest request) {

        Optional<User> user = userService.login(loginDto.getEmail(), loginDto.getPassword());

        if (user.isPresent()) {
            sessionManager.createSession(user.get().getAdPk(), request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "email", user.get().getEmail(),
                    "adminName", user.get().getAdminName(),
                    "adPk", user.get().getAdPk()

            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "이메일 또는 비밀번호가 올바르지 않습니다."
        ));
    }
    // 로그아웃 메서드 (세션 무효화)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(HttpServletRequest request) {

        sessionManager.expireSession(request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "로그아웃이 완료되었습니다."
        ));
    }

    // 관리자 번호 찾기 메서드
    @PostMapping("/email/re")
    public ResponseEntity<Map<String, Object>> retrieveAdminId(@RequestBody Map<String, String> params) {

        String adminName = params.get("adminName");
        String idNumber = params.get("idNumber");

        if (adminName == null || idNumber == null || adminName.isBlank() || idNumber.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이름과 주민등록번호를 입력해주세요."
            ));
        }

        Optional<User> user = userService.findUserByAdminNameAndIdNumber(adminName, idNumber);

        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "이메일 조회 성공",
                    "email", user.get().getEmail()
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "일치하는 사용자를 찾을 수 없습니다."
        ));
    }
    //비밀번호 찾기 전 본인 인증
    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticateAdmin(@RequestBody Map<String, String> params,
                                                                 HttpServletRequest request) {

        String email = params.get("email");
        String idNumber = params.get("idNumber");

        if (email == null || idNumber == null || email.isBlank() || idNumber.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이메일과 주민등록번호를 입력해주세요."
            ));
        }

        Optional<User> userOpt = userService.findByEmailAndIdNumber(email, idNumber);

        if (userOpt.isPresent()) {
            sessionManager.createSession(userOpt.get().getAdPk(), request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "본인 인증이 완료되었습니다."
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "일치하는 사용자를 찾을 수 없습니다."
        ));
    }

    //비밀번호 재설정
    @PostMapping("/password/re")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> params,
                                                             HttpServletRequest request) {

        String newPassword = params.get("newPassword");

        Long adPk = sessionManager.getAdPk(request);

        if (adPk == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "인증이 필요합니다."
            ));
        }

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "새 비밀번호를 입력해주세요."
            ));
        }

        Optional<User> user = userService.findById(adPk);

        if (user.isPresent()) {
            userService.updatePassword(user.get(), newPassword);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "비밀번호 초기화가 완료되었습니다."
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "사용자를 찾을 수 없습니다."
        ));
    }

}
