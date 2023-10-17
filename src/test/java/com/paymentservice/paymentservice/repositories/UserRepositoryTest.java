package com.paymentservice.paymentservice.repositories;

import com.paymentservice.paymentservice.domain.user.User;
import com.paymentservice.paymentservice.domain.user.UserType;
import com.paymentservice.paymentservice.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get user succesfully from database")
    void findUserByDocumentSuccess() {
        String document = "999999999";
        UserDTO data = new UserDTO("Joao", "Teste", document,
                new BigDecimal(10), "email@teste.com", "123123", UserType.COMMON);
        this.createUser(data);

        Optional<User> user = this.userRepository.findUserByDocument(document);

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get user succesfully from database")
    void findUserByDocumentFail() {
        String document = "999999999";

        Optional<User> user = this.userRepository.findUserByDocument(document);

        assertThat(user.isEmpty()).isTrue();
    }

    private User createUser(UserDTO data) {
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}