package com.example.friendmanagementsystem.controller;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(Controller.class)
@Import(AccountControllerTest.TestConfig.class)
class AccountControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountService accountService; // Spring-managed mock

    @TestConfiguration
    static class TestConfig {
        @Bean
        AccountService accountService() {
            return mock(AccountService.class); // Mockito mock
        }
    }

    @Test
    void addFriend_api_success() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("a@example.com", "b@example.com"));

        // Mock service response
        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(true)));

        // Perform POST request
        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void addFriend_api_userNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        // Mock service to simulate user not found error
        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_NOT_FOUND)));

        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1002)
                .jsonPath("$.message").isEqualTo("User not found")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void addFriend_api_invalidRequest() { // Invalid request (not exactly 2 emails)
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com")); // only 1 email -> invalid

        // Mock service to simulate invalid request
        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.INVALID_REQUEST)));

        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1001)
                .jsonPath("$.message").isEqualTo("Invalid request")
                .jsonPath("$.status").isEqualTo("error");
    }


    @Test
    void addFriend_api_sameEmails() { // Fail case: Emails must be different
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "andy@example.com")); // same email twice

        // Mock service to simulate SAME_EMAILS error
        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.SAME_EMAILS)));

        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1009)
                .jsonPath("$.message").isEqualTo("Emails must be different")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void addFriend_api_alreadyFriends() { // Fail case: Users already friends
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.ALREADY_FRIENDS)));

        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1003)
                .jsonPath("$.message").isEqualTo("Users are already friends")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void addFriend_api_blockedUser() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_CONNECTION_BLOCKED)));

        webTestClient.post()
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1010)
                .jsonPath("$.message").isEqualTo("You or this user has blocked the other")
                .jsonPath("$.status").isEqualTo("error");
    }
}

