package com.neocompany.taroro.domain.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 이메일 발송 서비스 로직
 * 사용자의 이메일 주소로 만들어진 인증코드(Verification Code)를 전송하는 기능을 담당한다.
 * JavaMailSender주입받아 SMTP(메일 서버)를 통해 메일을 보낸다.
 * Gmail SMTP 설정 사용(배포시 회사 계정 필요)
 * application.yml에 설정된 계정을 사용한다.
 *  1) 인증코드 생성 로직(별도 클래스)에서 code를 생성
 *  2) sendVerificationCode() 호출 시 수신자 이메일(toEmail)과 코드(code)를 전달
 *  3) SimpleMailMessage 로 제목/본문을 구성
 *  4) mailSender.send(msg) 호출 → 실제 SMTP 서버로 메일 발송
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("[Taroro] 이메일 인증 코드");  // 메일 제목
        // 메일 본문 내용
        msg.setText("""                                 
                아래 인증코드를 입력해 인증을 완료해주세요.
                
                인증코드: %s
                """.formatted(code));
        mailSender.send(msg);
    }
}