package com.example.pos_app.Controller;

import com.example.pos_app.Model.User;
import com.example.pos_app.Model.SessionManager;
import com.example.pos_app.DTO.UserDTO;
import com.example.pos_app.DTO.UserLoginDTO;
import com.example.pos_app.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
        response.put("adminId", newUser.getAdminId());
        response.put("adminName", newUser.getAdminName());
        response.put("idNumber", newUser.getIdNumber());
        response.put("phone", newUser.getPhone());
        response.put("password", newUser.getPassword());
        response.put("createdDate", newUser.getCreatedDate());
        System.out.println("회원가입 성공");
        // 생성된 adminId 출력
        System.out.println("생성된 adminId: " + newUser.getAdminId());
        // JSON 데이터와 함께 201 Created 상태 코드 반환
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserLoginDTO loginDto, HttpServletRequest request) {
        Optional<User> user = userService.login(loginDto.getAdminId(), loginDto.getPassword());

        Map<String, Object> response = new HashMap<>();

        if (user.isPresent()) {
            // 로그인 성공 시 세션에 adminId 저장
            sessionManager.createSession(user.get().getAdminId(), request);
            System.out.println(request);

            // JSON 응답 데이터 구성
            response.put("success", "true");
            response.put("adminId", user.get().getAdminId());
            response.put("adminName", user.get().getAdminName());

            System.out.println("JSON 응답 데이터: " + response);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // 로그인 실패 응답
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // 로그아웃 메서드 (세션 무효화)
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        sessionManager.expireSession(request);  // 세션 무효화
        System.out.println("로그아웃 성공");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 관리자 번호 찾기 메서드
    @PostMapping("/adminid/re")
    public ResponseEntity<Map<String, Object>> retrieveAdminId(@RequestBody Map<String, String> params) {
        String adminName = params.get("adminName");
        String idNumber = params.get("idNumber");
        // adminName 또는 idNumber가 null일 경우 에러 응답
        if (adminName == null || idNumber == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 입력된 adminName과 idNumber 출력
        System.out.println("Received adminName: " + adminName);
        System.out.println("Received idNumber: " + idNumber);
        Optional<User> user = userService.findUserByAdminNameAndIdNumber(adminName, idNumber);

        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getAdminId()); // 찾은 adminId 출력

            // JSON 형식으로 adminId 반환
            Map<String, Object> response = new HashMap<>();
            response.put("adminId", user.get().getAdminId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            System.out.println("User not found for adminName: " + adminName + " and idNumber: " + idNumber); // 유저가 없을 때 출력

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticateAdmin(@RequestBody Map<String, String> params, HttpServletRequest  request) {
        String adminId = params.get("adminId");
        String idNumber = params.get("idNumber");
        Map<String, Object> response = new HashMap<>();

        // 입력 값 유효성 검사
        if (adminId == null || idNumber == null) {
            response.put("status", false);
            response.put("message", "Missing required fields");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 사용자 조회
        Optional<User> user = userService.findByAdminIdAndIdNumber(adminId, idNumber);

        if (user.isPresent()) {
            // 세션에 adminId 저장
            sessionManager.createSession(adminId, request);
            System.out.println(request);
            response.put("status", true);
            response.put("message", "Authentication successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", false);
            response.put("message", "Authentication failed. User not found");
            System.out.println(adminId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //비밀번호 재설정
    @PostMapping("/password/re")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> params, HttpServletRequest  request) {
        String newPassword = params.get("newPassword");

        Map<String, String> response = new HashMap<>();

        // 세션에서 adminId 가져오기 (인증된 adminId)
        String adminId = sessionManager.getAdminId(request);
        System.out.println("adminId 가져옴: "+adminId);
        if (adminId == null) {
            System.out.println("adminId없음");
            System.out.println(adminId);
            response.put("status", "failure");
            response.put("message", "Authentication required");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 입력 값 유효성 검사
        if (newPassword == null || newPassword.isBlank()) {
            response.put("status", "failure");
            response.put("message", "New password is missing");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // adminId로 사용자 조회
        Optional<User> user = userService.findByAdminId(adminId);

        if (user.isPresent()) {
            // 비밀번호 재설정
            userService.updatePassword(user.get(), newPassword);


            response.put("status", "success");
            response.put("message", "비밀번호 초기화가 완료되었습니다.");

            // 비밀번호 변경 후 세션에 갱신된 사용자 adminId 저장
            request.setAttribute("adminId", user.get().getAdminId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {

            response.put("status", "failure");
            response.put("message", "사용자를 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/myinfo")
    public ResponseEntity<Map<String, String>> getAdminId(HttpSession session) {
        // 세션에서 adminId 가져오기
        String adminId = (String) session.getAttribute("adminId");

        // adminId가 없으면 UNAUTHORIZED 응답 반환
        if (adminId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // adminId를 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("adminId", adminId);


        return ResponseEntity.ok(response);
    }

}
