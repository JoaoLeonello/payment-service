package com.paymentservice.paymentservice.services;

import com.paymentservice.paymentservice.domain.transaction.Transaction;
import com.paymentservice.paymentservice.domain.user.User;
import com.paymentservice.paymentservice.dtos.TransactionDTO;
import com.paymentservice.paymentservice.repositories.TransactionRepository;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthorizationService authroizationService;

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = this.authroizationService.authorizeTransaction(sender, transactionDTO.value());
        if(!isAuthorized) {
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionDTO.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        repository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        this.notificationService.sendNotification(sender, "Transação recebida com sucesso");

        return newTransaction;
    }
}
