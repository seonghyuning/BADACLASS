package com.ocean.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // SMTP 서버 및 포트 설정
        mailSender.setHost("smtp.gmail.com");				
        mailSender.setPort(587);	// TLS 사용
        
        // 계정 및 인증 설정
        mailSender.setUsername("youwin0918@gmail.com");		
        mailSender.setPassword("cmbs fneq guvw vqdp");	// 2단계 앱 비밀번호
        
        // SSL/TLS 설정 추가
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
