package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.BlockRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepo;
    @Mock
    private FriendRepository friendRepo;
    //@Mock
    //private FollowerRepository followerRepo;
    @Mock
    private BlockRepository blockRepo;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void addFriend_success() {
        Account acc1 = new Account(1, "a@example.com");
        Account acc2 = new Account(2, "b@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("a@example.com", "b@example.com"));

        when(accountRepo.findByEmail("a@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("b@example.com")).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));
        when(friendRepo.existsByUserId1AndUserId2(1, 2)).thenReturn(Mono.just(false));
        when(friendRepo.save(any(Friend.class))).thenReturn(Mono.just(new Friend(1, 2)));

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(ApiResponseDTO::getSuccess)
                .verifyComplete();
    }

    @Test
    void addFriend_userNotFound() {
        var dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountRepo.findByEmail(anyString())).thenReturn(Mono.empty()); //return empty

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1002
                        && resp.getMessage().equals("User not found"))
                .verifyComplete();
    }

    @Test
    void addFriend_invalidRequest() { // Invalid request (not 2 emails)
        var dto = new AccountDTO();
        dto.setFriends(List.of("onlyone@example.com")); // only 1 email

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1001
                        && resp.getMessage().equals("Invalid request"))
                .verifyComplete();
    }

    @Test
    void addFriend_sameEmails() { // Same emails
        var dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "andy@example.com"));

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1009
                        && resp.getMessage().equals("Emails must be different"))
                .verifyComplete();
    }

    @Test
    void addFriend_alreadyFriends() { //Already friends
        var acc1 = new Account(1, "a@example.com");
        var acc2 = new Account(2, "b@example.com");
        var dto = new AccountDTO();
        dto.setFriends(List.of(acc1.getEmail(), acc2.getEmail()));

        when(accountRepo.findByEmail(acc1.getEmail())).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail(acc2.getEmail())).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));
        when(friendRepo.existsByUserId1AndUserId2(anyInt(), anyInt())).thenReturn(Mono.just(true)); // already friends

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1003
                        && resp.getMessage().equals("Users are already friends"))
                .verifyComplete();
    }

    @Test
    void addFriend_blockedUsers() {
        var acc1 = new Account(1, "a@example.com");
        var acc2 = new Account(2, "b@example.com");
        var dto = new AccountDTO();
        dto.setFriends(List.of(acc1.getEmail(), acc2.getEmail()));

        when(accountRepo.findByEmail(acc1.getEmail())).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail(acc2.getEmail())).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(acc1.getUserId(), acc2.getUserId()))
                .thenReturn(Mono.just(true)); // blocked
        when(blockRepo.existsByBlockerIdAndBlockedId(acc2.getUserId(), acc1.getUserId()))
                .thenReturn(Mono.just(false));
        when(friendRepo.existsByUserId1AndUserId2(anyInt(), anyInt()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1010
                        && resp.getMessage().equals("You or this user has blocked the other"))
                .verifyComplete();
    }


}
