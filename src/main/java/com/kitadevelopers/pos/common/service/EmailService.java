package com.kitadevelopers.pos.common.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void send(String to, String resetLink){
        System.out.println("Send Email to" + to);
        System.out.println("Reset Link" + resetLink);
//        integrate SMTP / SendGrid / Resend later
    }
}
