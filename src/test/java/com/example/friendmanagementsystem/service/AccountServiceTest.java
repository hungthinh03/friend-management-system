package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.BlockRepository;
import com.example.friendmanagementsystem.repository.FollowerRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepo;
    @Mock
    private FriendRepository friendRepo;
    @Mock
    private FollowerRepository followerRepo;
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
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false)); // check both ways
        when(friendRepo.existsByUserId1AndUserId2(1, 2)).thenReturn(Mono.just(false));
        when(friendRepo.save(any(Friend.class))).thenReturn(Mono.just(new Friend(1, 2)));

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(ApiResponseDTO::getSuccess)
                .verifyComplete();
    }

    /*
    @Test
    void addFriend_alreadyFriends() {
        Account acc1 = new Account(1, "a@example.com");
        Account acc2 = new Account(2, "b@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("a@example.com", "b@example.com"));

        when(accountRepo.findByEmail("a@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("b@example.com")).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));
        when(friendRepo.existsByUserId1AndUserId2(1, 2)).thenReturn(Mono.just(true));

        StepVerifier.create(accountService.addFriend(dto))
                .expectNextMatches(resp -> resp.getErrorCode() == ErrorCode.ALREADY_FRIENDS)
                .verifyComplete();
    }
    */

}
