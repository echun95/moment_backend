package com.moment.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String momentAdminEmail;

    public void sendMessage(String to, String from, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
    public void sendEmailCertification(String to, int number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(momentAdminEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Moment email 인증 메일 발송입니다.");
        String body = "";
        body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>" + "감사합니다." + "</h3>";
        message.setText(body,"UTF-8", "html");
        javaMailSender.send(message);
    }
    public void sendTemporaryPassword(String to, String temporaryPassword) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(momentAdminEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Moment 임시 비밀번호 메일 발송입니다.");
        String body = "";
        body += "<h3>" + "요청하신 임시 비밀번호입니다." + "</h3>";
        body += "<h1>" + temporaryPassword + "</h1>";
        body += "<h3>" + "임시 비밀번호 유효시간은 10분입니다." + "</h3>";
        body += "<h3>" + "감사합니다." + "</h3>";
        message.setText(body,"UTF-8", "html");
        javaMailSender.send(message);
    }

}
