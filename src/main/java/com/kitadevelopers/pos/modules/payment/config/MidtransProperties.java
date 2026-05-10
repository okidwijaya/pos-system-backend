package com.kitadevelopers.pos.modules.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "midtrans")
public class MidtransProperties {
    private String serverKey;
    private String snapUrl = "https://app.sandbox.midtrans.com/snap/v1/transactions";
}
