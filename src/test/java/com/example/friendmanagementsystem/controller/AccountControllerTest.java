package com.example.friendmanagementsystem.controller;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.dto.UserConnectionDTO;
import com.example.friendmanagementsystem.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
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

    // add friend
    @Test
    void addFriend_success() {
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
    void addFriend_userNotFound() {
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
    void addFriend_invalidRequest() { // Invalid request (not exactly 2 emails)
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
    void addFriend_sameEmails() { // Fail case: Emails must be different
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
    void addFriend_alreadyFriends() { // Fail case: Users already friends
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
    void addFriend_blockedUser() {
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

    @Test
    void removeFriend_success() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountService.removeFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(true)));

        webTestClient
                .method(HttpMethod.DELETE)
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void removeFriend_notFriends() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountService.removeFriend(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.NOT_FRIENDS)));

        webTestClient
                .method(HttpMethod.DELETE)
                .uri("/account")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("error")
                .jsonPath("$.message").isEqualTo("Users are not friends")
                .jsonPath("$.statusCode").isEqualTo(1004);
    }


    // Get friends
    @Test
    void getFriends_success() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("andy@example.com");

        ApiResponseDTO response = new ApiResponseDTO(true, List.of("john@example.com"), 1);

        when(accountService.getFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/account/friends")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.friends[0]").isEqualTo("john@example.com")
                .jsonPath("$.count").isEqualTo(1);
    }

    @Test
    void getFriends_userNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("andy@example.com");

        when(accountService.getFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_NOT_FOUND)));

        webTestClient.post()
                .uri("/account/friends")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("error")
                .jsonPath("$.message").isEqualTo("User not found")
                .jsonPath("$.statusCode").isEqualTo(1002);
    }


    // Get mutual
    @Test
    void getCommonFriends_success() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        // Mock service response
        when(accountService.getCommonFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(true, List.of("common@example.com"), 1)));

        webTestClient.post()
                .uri("/account/friend/mutual")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.friends[0]").isEqualTo("common@example.com")
                .jsonPath("$.count").isEqualTo(1);
    }

    @Test
    void getCommonFriends_invalidRequest() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("onlyone@example.com"));

        when(accountService.getCommonFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.INVALID_REQUEST)));

        webTestClient.post()
                .uri("/account/friend/mutual")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1001)
                .jsonPath("$.message").isEqualTo("Invalid request")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void getCommonFriends_userNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountService.getCommonFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_NOT_FOUND)));

        webTestClient.post()
                .uri("/account/friend/mutual")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1002)
                .jsonPath("$.message").isEqualTo("User not found")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void getCommonFriends_sameEmails() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "andy@example.com"));

        when(accountService.getCommonFriends(any(AccountDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.SAME_EMAILS)));

        webTestClient.post()
                .uri("/account/friend/mutual")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1009)
                .jsonPath("$.message").isEqualTo("Emails must be different")
                .jsonPath("$.status").isEqualTo("error");
    }


    // Follow
    @Test
    void subscribeUpdates_success() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "john@example.com");

        when(accountService.subscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(true)));

        webTestClient.post()
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void subscribeUpdates_userNotFound() {
        UserConnectionDTO dto = new UserConnectionDTO("ghost@example.com", "john@example.com");

        when(accountService.subscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_NOT_FOUND)));

        webTestClient.post()
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1002)
                .jsonPath("$.message").isEqualTo("User not found")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void subscribeUpdates_sameEmail() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "lisa@example.com");

        when(accountService.subscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.SAME_EMAILS)));

        webTestClient.post()
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1009)
                .jsonPath("$.message").isEqualTo("Emails must be different")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void subscribeUpdates_blocked() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "john@example.com");

        when(accountService.subscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.USER_CONNECTION_BLOCKED)));

        webTestClient.post()
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1010)
                .jsonPath("$.message").isEqualTo("You or this user has blocked the other")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void subscribeUpdates_alreadyFollowed() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "john@example.com");

        when(accountService.subscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.ALREADY_FOLLOWED)));

        webTestClient.post()
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1005)
                .jsonPath("$.message").isEqualTo("User is already followed")
                .jsonPath("$.status").isEqualTo("error");
    }

    @Test
    void unsubscribeUpdates_success() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "john@example.com");

        when(accountService.unsubscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(true)));

        webTestClient.method(HttpMethod.DELETE)
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void unsubscribeUpdates_notFollowed() {
        UserConnectionDTO dto = new UserConnectionDTO("lisa@example.com", "john@example.com");

        when(accountService.unsubscribeUpdates(dto))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.NOT_FOLLOWED)));

        webTestClient.method(HttpMethod.DELETE)
                .uri("/account/follow")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1006)
                .jsonPath("$.message").isEqualTo("User is not followed")
                .jsonPath("$.status").isEqualTo("error");
    }
}

