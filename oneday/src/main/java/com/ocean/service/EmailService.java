package com.ocean.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 정해진 이메일을 받아서 이메일을 보내주는 메서드
    public void sendReservationConfirmation(String to, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);				// 수신자 이메일
        mailMessage.setSubject(subject);	// 이메일 제목
        mailMessage.setText(message);		// 이메일 내용
        mailSender.send(mailMessage);
    }
}
