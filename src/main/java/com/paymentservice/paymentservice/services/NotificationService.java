package com.paymentservice.paymentservice.services;

import com.paymentservice.paymentservice.domain.user.User;
import com.paymentservice.paymentservice.dtos.NotificationDTO;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationDTO = new NotificationDTO(email, message);

        ResponseEntity<String> notificationResponse =
            restTemplate.postForEntity("(http://o4d9z.mocklab.io/notify", notificationDTO, String.class);

        if(!(notificationResponse.getStatusCode() == HttpStatus.OK)) {
            System.out.println("erro ao enviar notificação");
            throw new Exception("Serviço de notificação indisponível");
        }
    }
}
