package com.kitadevelopers.pos.modules.payment.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class SignatureVerifier {

    private static final String SERVER_KEY = "MY_MIDTRANS_KEY";

    public boolean verify(
            String orderId,
            String statusCode,
            String grossAmount,
            String signature
    ){
        String raw = orderId + statusCode + grossAmount + SERVER_KEY;

        String hashed = DigestUtils.sha512Hex(raw);

        return hashed.equals(signature);
    }
}
