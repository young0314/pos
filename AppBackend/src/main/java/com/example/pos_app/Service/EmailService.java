package com.example.pos_app.Service;

import com.example.pos_app.Config.RedisUtil;
import com.example.pos_app.DTO.EmailAuthResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    // 인증코드 전송
    public EmailAuthResponseDTO sendEmail(String toEmail) {
        String normalizedEmail = toEmail.trim();

        // 기존 인증코드가 있으면 삭제
        if (redisUtil.existData(normalizedEmail)) {
            redisUtil.deleteData(normalizedEmail);
        }

        try {
            MimeMessage emailForm = createEmailForm(normalizedEmail);
            mailSender.send(emailForm);
            return new EmailAuthResponseDTO(true, "인증번호가 메일로 전송되었습니다.");
        } catch (MessagingException | MailSendException e) {
            return new EmailAuthResponseDTO(false, "메일 전송 중 오류가 발생하였습니다. 다시 시도해주세요.");
        }
    }

    // 이메일 양식 생성 + Redis 저장
    private MimeMessage createEmailForm(String email) throws MessagingException {
        String authCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("인증코드입니다.");
        message.setText(setContext(authCode), "utf-8", "html");

        // 인증코드 3분 유효
        redisUtil.setDataExpire(email, authCode, 3 * 60L);

        return message;
    }

    // 메일 본문
    private String setContext(String authCode) {
        String body = "";
        body += "<h4>인증 코드를 입력하세요.</h4>";
        body += "<h2>[" + authCode + "]</h2>";
        body += "<p>인증코드는 3분 동안만 유효합니다.</p>";
        return body;
    }

    // 인증코드 검증
    public EmailAuthResponseDTO validateAuthCode(String email, String authCode) {
        String normalizedEmail = email.trim();
        String normalizedAuthCode = authCode.trim();

        String findAuthCode = redisUtil.getData(normalizedEmail);

        if (findAuthCode == null) {
            return new EmailAuthResponseDTO(false, "인증번호가 만료되었습니다. 다시 시도해주세요.");
        }

        if (findAuthCode.trim().equals(normalizedAuthCode)) {
            // 인증 완료 상태 저장 (회원가입용)
            redisUtil.setDataExpire("verified:" + normalizedEmail, "true", 10 * 60L);

            // 기존 인증코드 삭제
            redisUtil.deleteData(normalizedEmail);

            return new EmailAuthResponseDTO(true, "인증 성공했습니다.");
        } else {
            return new EmailAuthResponseDTO(false, "인증번호가 일치하지 않습니다.");
        }
    }
}