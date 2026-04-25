package com.kitadevelopers.pos.modules.payment.controller;

import com.kitadevelopers.pos.modules.payment.dto.WebhookRequest;
import com.kitadevelopers.pos.modules.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    final WebhookService service;

    @PostMapping("/payment")
    public ResponseEntity<String> webhook(@RequestBody WebhookRequest request){
        service.handle(request);
        return ResponseEntity.ok("OK");
    }
}
