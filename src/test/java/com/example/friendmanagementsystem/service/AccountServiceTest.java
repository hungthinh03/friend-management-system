package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.dto.PostDTO;
import com.example.friendmanagementsystem.dto.UserConnectionDTO;
import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Block;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.model.Follower;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.BlockRepository;
import com.example.friendmanagementsystem.repository.FollowerRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
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

    // Unfriend
    @Test
    void removeFriend_success() {
        Account acc1 = new Account(1, "a@example.com");
        Account acc2 = new Account(2, "b@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("a@example.com", "b@example.com"));

        when(accountRepo.findByEmail("a@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("b@example.com")).thenReturn(Mono.just(acc2));
        when(friendRepo.existsByUserId1AndUserId2(1, 2)).thenReturn(Mono.just(true));
        when(friendRepo.deleteByUserId1AndUserId2(1, 2)).thenReturn(Mono.empty());

        StepVerifier.create(accountService.removeFriend(dto))
                .expectNextMatches(ApiResponseDTO::getSuccess)
                .verifyComplete();
    }

    @Test
    void removeFriend_notFriends() {
        Account acc1 = new Account(1, "a@example.com");
        Account acc2 = new Account(2, "b@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("a@example.com", "b@example.com"));

        when(accountRepo.findByEmail("a@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("b@example.com")).thenReturn(Mono.just(acc2));
        when(friendRepo.existsByUserId1AndUserId2(1, 2)).thenReturn(Mono.just(false));

        StepVerifier.create(accountService.removeFriend(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1004
                        && resp.getMessage().equals("Users are not friends"))
                .verifyComplete();
    }

    // Get friends
    @Test
    void getFriends_success() {
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setEmail("andy@example.com");

        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(acc1));
        when(friendRepo.findAllFriendIdsByUserId(1)).thenReturn(Flux.just(2));
        when(accountRepo.findAllById(List.of(2))).thenReturn(Flux.just(acc2));

        StepVerifier.create(accountService.getFriends(dto))
                .expectNextMatches(res ->
                        res.getSuccess()
                                && res.getFriends().contains("john@example.com")
                                && res.getCount() == 1
                )
                .verifyComplete();
    }

    @Test
    void getFriends_userNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("missing@example.com");

        when(accountRepo.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getFriends(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1002
                        && resp.getMessage().equals("User not found"))
                .verifyComplete();
    }


    // Get mutual
    @Test
    void getCommonFriends_success() {
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of(acc1.getEmail(), acc2.getEmail()));

        when(accountRepo.findByEmail(acc1.getEmail())).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail(acc2.getEmail())).thenReturn(Mono.just(acc2));

        // Mock friends of both users
        when(friendRepo.findAllFriendIdsByUserId(acc1.getUserId()))
                .thenReturn(Flux.just(3, 4)); // ids of friends
        when(friendRepo.findAllFriendIdsByUserId(acc2.getUserId()))
                .thenReturn(Flux.just(4, 5)); // ids of friends

        when(accountRepo.findAllById(Set.of(4))).thenReturn(Flux.just(new Account(4, "common@example.com")));

        StepVerifier.create(accountService.getCommonFriends(dto))
                .expectNextMatches(resp -> resp.getSuccess() != null
                        && resp.getSuccess()
                        && resp.getFriends().size() == 1
                        && resp.getFriends().contains("common@example.com")
                        && resp.getCount() == 1)
                .verifyComplete();
    }

    @Test
    void getCommonFriends_userNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "john@example.com"));

        when(accountRepo.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getCommonFriends(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1002
                        && resp.getMessage().equals("User not found"))
                .verifyComplete();
    }

    @Test
    void getCommonFriends_invalidRequest() {
        AccountDTO dto = new AccountDTO();
        dto.setFriends(List.of("onlyone@example.com"));

        StepVerifier.create(accountService.getCommonFriends(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1001
                        && resp.getMessage().equals("Invalid request"))
                .verifyComplete();
    }

    @Test
    void getCommonFriends_sameEmails() {
        var dto = new AccountDTO();
        dto.setFriends(List.of("andy@example.com", "andy@example.com"));

        StepVerifier.create(accountService.getCommonFriends(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1009
                        && resp.getMessage().equals("Emails must be different"))
                .verifyComplete();
    }

    // Follow
    @Test
    void subscribeUpdates_success() {
        Account lisa = new Account(1, "lisa@example.com");
        Account john = new Account(2, "john@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), john.getEmail());

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail(john.getEmail())).thenReturn(Mono.just(john));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));
        when(followerRepo.existsByFollowerIdAndFolloweeId(1, 2)).thenReturn(Mono.just(false));
        when(followerRepo.save(any(Follower.class))).thenReturn(Mono.just(new Follower(1, 2)));

        StepVerifier.create(accountService.subscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getSuccess() != null
                        && resp.getSuccess())
                .verifyComplete();
    }

    @Test
    void subscribeUpdates_userNotFound() {
        Account lisa = new Account(1, "lisa@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), "unknown@example.com");

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail("unknown@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(accountService.subscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1002 &&
                        resp.getMessage().equals("User not found"))
                .verifyComplete();
    }

    @Test
    void subscribeUpdates_sameEmails() {
        Account lisa = new Account(1, "lisa@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), lisa.getEmail());

        StepVerifier.create(accountService.subscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1009 &&
                        resp.getMessage().equals("Emails must be different"))
                .verifyComplete();
    }

    @Test
    void subscribeUpdates_blocked() {
        Account lisa = new Account(1, "lisa@example.com");
        Account john = new Account(2, "john@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), john.getEmail());

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail(john.getEmail())).thenReturn(Mono.just(john));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(true));
        when(followerRepo.existsByFollowerIdAndFolloweeId(anyInt(), anyInt())).thenReturn(Mono.just(false));

        StepVerifier.create(accountService.subscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1010 &&
                        resp.getMessage().equals("You or this user has blocked the other"))
                .verifyComplete();
    }

    @Test
    void subscribeUpdates_alreadyFollowed() {
        Account lisa = new Account(1, "lisa@example.com");
        Account john = new Account(2, "john@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), john.getEmail());

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail(john.getEmail())).thenReturn(Mono.just(john));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));
        when(followerRepo.existsByFollowerIdAndFolloweeId(1, 2)).thenReturn(Mono.just(true));

        StepVerifier.create(accountService.subscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1005 &&
                        resp.getMessage().equals("User is already followed"))
                .verifyComplete();
    }

    @Test
    void unsubscribeUpdates_success() {
        Account lisa = new Account(1, "lisa@example.com");
        Account john = new Account(2, "john@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), john.getEmail());

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail(john.getEmail())).thenReturn(Mono.just(john));

        when(followerRepo.existsByFollowerIdAndFolloweeId(lisa.getUserId(), john.getUserId()))
                .thenReturn(Mono.just(true));
        when(followerRepo.deleteByFollowerIdAndFolloweeId(lisa.getUserId(), john.getUserId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.unsubscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getSuccess() != null
                        && resp.getSuccess())
                .verifyComplete();
    }

    @Test
    void unsubscribeUpdates_notFollowed() {
        Account lisa = new Account(1, "lisa@example.com");
        Account john = new Account(2, "john@example.com");
        var dto = new UserConnectionDTO(lisa.getEmail(), john.getEmail());

        when(accountRepo.findByEmail(lisa.getEmail())).thenReturn(Mono.just(lisa));
        when(accountRepo.findByEmail(john.getEmail())).thenReturn(Mono.just(john));

        when(followerRepo.existsByFollowerIdAndFolloweeId(lisa.getUserId(), john.getUserId()))
                .thenReturn(Mono.just(false)); // not following

        StepVerifier.create(accountService.unsubscribeUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1006
                        && resp.getMessage().equals("User is not followed"))
                .verifyComplete();
    }


    // Block
    @Test
    void blockUpdates_success() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "john@example.com");
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("john@example.com")).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(1, 2)).thenReturn(Mono.just(false));
        when(blockRepo.save(any(Block.class))).thenReturn(Mono.just(new Block(1, 2)));
        when(followerRepo.deleteByFollowerIdAndFolloweeId(anyInt(), anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.blockUpdates(dto))
                .expectNextMatches(ApiResponseDTO::getSuccess)
                .verifyComplete();
    }

    @Test
    void blockUpdates_userNotFound() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "ghost@example.com");
        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(new Account(1, "andy@example.com")));
        when(accountRepo.findByEmail("ghost@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(accountService.blockUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1002
                        && resp.getMessage().equals("User not found"))
                .verifyComplete();
    }

    @Test
    void blockUpdates_sameEmails() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "andy@example.com");

        StepVerifier.create(accountService.blockUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1009
                        && resp.getMessage().equals("Emails must be different"))
                .verifyComplete();
    }

    @Test
    void blockUpdates_alreadyBlocked() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "john@example.com");
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("john@example.com")).thenReturn(Mono.just(acc2));
        when(blockRepo.existsByBlockerIdAndBlockedId(1, 2)).thenReturn(Mono.just(true));

        StepVerifier.create(accountService.blockUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1007
                        && resp.getMessage().equals("User is already blocked"))
                .verifyComplete();
    }

    @Test
    void unblockUpdates_success() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "john@example.com");
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        // Mock account fetching
        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("john@example.com")).thenReturn(Mono.just(acc2));


        when(blockRepo.existsByBlockerIdAndBlockedId(acc1.getUserId(), acc2.getUserId()))
                .thenReturn(Mono.just(true));
        when(blockRepo.deleteByBlockerIdAndBlockedId(acc1.getUserId(), acc2.getUserId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.unblockUpdates(dto))
                .expectNextMatches(resp -> resp.getSuccess() != null && resp.getSuccess())
                .verifyComplete();
    }

    @Test
    void unblockUpdates_notBlocked() {
        UserConnectionDTO dto = new UserConnectionDTO("andy@example.com", "john@example.com");
        Account acc1 = new Account(1, "andy@example.com");
        Account acc2 = new Account(2, "john@example.com");

        when(accountRepo.findByEmail("andy@example.com")).thenReturn(Mono.just(acc1));
        when(accountRepo.findByEmail("john@example.com")).thenReturn(Mono.just(acc2));

        when(blockRepo.existsByBlockerIdAndBlockedId(acc1.getUserId(), acc2.getUserId()))
                .thenReturn(Mono.just(false));
        StepVerifier.create(accountService.unblockUpdates(dto))
                .expectNextMatches(resp -> resp.getStatusCode() == 1008
                        && resp.getMessage().equals("User is not blocked"))
                .verifyComplete();
    }


    // Get recipients
    @Test
    void getUpdateRecipients_success() {
        PostDTO dto = new PostDTO("john@example.com", "Hello! kate@example.com");
        Account sender = new Account(1, "john@example.com");
        Account friend = new Account(2, "lisa@example.com");
        Account follower = new Account(3, "andy@example.com");
        Account mentioned = new Account(4, "kate@example.com");

        when(accountRepo.findByEmail(sender.getEmail())).thenReturn(Mono.just(sender));
        when(friendRepo.findAllFriendIdsByUserId(sender.getUserId())).thenReturn(Flux.just(friend.getUserId()));
        when(followerRepo.findByFolloweeId(sender.getUserId())).thenReturn(Flux.just(follower.getUserId()));
        when(accountRepo.findById(friend.getUserId())).thenReturn(Mono.just(friend));
        when(accountRepo.findById(follower.getUserId())).thenReturn(Mono.just(follower));
        when(accountRepo.findByEmail(mentioned.getEmail())).thenReturn(Mono.just(mentioned));
        when(blockRepo.existsByBlockerIdAndBlockedId(anyInt(), anyInt())).thenReturn(Mono.just(false));

        StepVerifier.create(accountService.getUpdateRecipients(dto))
                .expectNextMatches(resp -> resp.getSuccess() != null && resp.getSuccess() &&
                                resp.getRecipients()
                                        .containsAll(List.of("lisa@example.com", "andy@example.com", "kate@example.com"))
                )
                .verifyComplete();
    }

    @Test
    void getUpdateRecipients_userNotFound() {
        PostDTO dto = new PostDTO("ghost@example.com", "Hello! kate@example.com");

        when(accountRepo.findByEmail("ghost@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getUpdateRecipients(dto))
                .expectNextMatches(resp ->
                        resp.getStatusCode() == 1002 &&
                                resp.getMessage().equals("User not found")
                )
                .verifyComplete();
    }

    @Test
    void getUpdateRecipients_invalidRequest() {
        PostDTO dto = new PostDTO("john@example.com", "");

        StepVerifier.create(accountService.getUpdateRecipients(dto))
                .expectNextMatches(resp ->
                        resp.getStatusCode() == 1001 &&
                                resp.getMessage().equals("Invalid request")
                )
                .verifyComplete();
    }
}

