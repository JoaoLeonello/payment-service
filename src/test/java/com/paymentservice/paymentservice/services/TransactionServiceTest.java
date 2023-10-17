package com.paymentservice.paymentservice.services;

import com.paymentservice.paymentservice.domain.user.User;
import com.paymentservice.paymentservice.domain.user.UserType;
import com.paymentservice.paymentservice.dtos.TransactionDTO;
import com.paymentservice.paymentservice.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthorizationService authroizationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void createTransactionSuccess() throws Exception {
        User sender = new User(1L, "Maria", "Souza",
                "9999999901", "maria@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);

        User receiver = new User(2L, "Joao", "Souza",
                "9999999901", "maria@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authroizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);

        verify(repository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso");
        verify(notificationService, times(1)).sendNotification(sender, "Transação recebida com sucesso");
    }

    @Test
    @DisplayName("Should not create transaction successfully")
    void createTransactionFail() throws Exception {
        User sender = new User(1L, "Maria", "Souza",
                "9999999901", "maria@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);

        User receiver = new User(2L, "Joao", "Souza",
                "9999999901", "maria@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authroizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
}