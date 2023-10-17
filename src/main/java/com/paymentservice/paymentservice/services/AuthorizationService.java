package com.paymentservice.paymentservice.services;

import com.paymentservice.paymentservice.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AuthorizationService {
    @Autowired
    RestTemplate restTemplate;

    public boolean authorizeTransaction(User sender, BigDecimal value) {
        ResponseEntity<Map> authorizationResponse =
                restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK) {
            String message = String.valueOf(authorizationResponse.getBody().get("message") == "Autorizado");
            return "Autorizado".equalsIgnoreCase((message));
        } else return false;

    }
}
