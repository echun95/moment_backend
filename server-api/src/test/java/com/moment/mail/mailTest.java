package com.moment.mail;

import com.moment.mail.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class mailTest {
    @Autowired
    private EmailService emailService;
    private String senderEmail= "dldydcns123@gmail.com";
    private static int number;

//    @Test
    void sendMailTest() throws MessagingException {
        emailService.sendEmailCertification(senderEmail, createNumber());
    }
    // 랜덤으로 숫자 생성
    public static int createNumber() {
        return (int)(Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }
}
