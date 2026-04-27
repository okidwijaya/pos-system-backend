package com.kitadevelopers.pos.modules.payment.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class SignatureVerifier {

    @Value("${midtrans.server-key}")
    private String serverKey;
//    private static final String SERVER_KEY = "MY_MIDTRANS_KEY";

    public boolean verify( String orderId, String statusCode, String grossAmount, String signature){

        try {
            String payload = orderId + statusCode + grossAmount + serverKey;

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().equals(signature);
        }catch (Exception e){
            throw new RuntimeException("Signature verification failed");
        }
//        String hashed = DigestUtils.sha512Hex(payload);
//        return hashed.equals(signature);
    }
}
