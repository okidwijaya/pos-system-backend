package com.kitadevelopers.pos.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${spring.mail.username:no-reply@localhost}")
    private String from;

    public void send(String to, String resetLink){
        JavaMailSender sender = mailSender.getIfAvailable();
        if(sender == null){
            throw new IllegalStateException("No mail sender configured");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reset your POS password");
        message.setText("Use this link to reset your password: " + resetLink);
        sender.send(message);
    }
}
