package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private FriendRepository friendRepo;

    private Mono<Map<String, Object>> fail(String message, int status) {
        return Mono.just(Map.of(
                "success", false,
                "message", message,
                "statusCode", status
        ));
    }

    @Override
    public Mono<AccountDTO> addFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter((List<String> list) -> list.size() == 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exactly 2 friends required")))
                .flatMap((List<String> friends) -> {
                    String email1 = friends.get(0);
                    String email2 = friends.get(1);

                    return Mono.zip(
                                    accountRepo.findByEmail(email1)
                                            .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + email1))),
                                    accountRepo.findByEmail(email2)
                                            .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + email2)))
                            )
                            .flatMap(tuple -> {
                                Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                                Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                                return friendRepo.save(new Friend(id1, id2))
                                        .thenReturn(dto); // returning the DTO or the first AccountDTO
                            });
                });
    }

}
