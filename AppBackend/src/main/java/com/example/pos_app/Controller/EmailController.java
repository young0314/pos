package com.example.pos_app.Controller;

import com.example.pos_app.DTO.EmailAuthCheckRequestDTO;
import com.example.pos_app.DTO.EmailAuthRequestDTO;
import com.example.pos_app.DTO.EmailAuthResponseDTO;
import com.example.pos_app.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/email")
public class EmailController {

    private final EmailService emailService;

    // 인증번호 전송
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendAuthCode(@RequestBody EmailAuthRequestDTO requestDto) {

        EmailAuthResponseDTO response = emailService.sendEmail(requestDto.getEmail());

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "이메일 전송 실패"
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "인증번호가 전송되었습니다."
        ));
    }

    // 인증번호 검증
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> checkAuthCode(@RequestBody EmailAuthCheckRequestDTO requestDto) {

        EmailAuthResponseDTO response =
                emailService.validateAuthCode(requestDto.getEmail(), requestDto.getAuthCode());

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "인증번호가 일치하지 않거나 만료되었습니다."
                    ));
        }

        System.out.println("이메일 인증 완료");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "이메일 인증 성공"
        ));
    }
}