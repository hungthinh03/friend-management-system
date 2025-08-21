package com.example.friendmanagementsystem.controller;

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

    /*
    @Test
    void addFriend_api_invalidRequest() {
        // Empty DTO triggers INVALID_REQUEST
        AccountDTO dto = new AccountDTO();

        when(accountService.addFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.INVALID_REQUEST)));

        webTestClient.post()
                .uri("/accounts/friends")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseDTO.class)
                .value(resp -> assertEquals(ErrorCode.INVALID_REQUEST, resp.getError().getCode()));
    }

     */
}

