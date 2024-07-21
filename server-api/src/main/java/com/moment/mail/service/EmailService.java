package com.moment.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendMessage(String to, String from, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
    public void sendEmailCertification(String to, String from, int number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(from);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Moment email 인증 메일 발송입니다.");
        String body = "";
        body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>" + "감사합니다." + "</h3>";
        message.setText(body,"UTF-8", "html");
        javaMailSender.send(message);
    }

}
