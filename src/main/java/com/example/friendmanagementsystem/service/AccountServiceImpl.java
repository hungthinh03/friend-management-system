package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private FriendRepository friendRepo;


    @Override
    public Mono<ApiResponseDTO> addFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exactly 2 friends required")))
                .flatMap(friends -> {
                    String email1 = friends.get(0);
                    String email2 = friends.get(1);

                    return Mono.zip( //Subscribes in parallel, waits for both to complete
                                    accountRepo.findByEmail(email1)
                                            .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + email1))),
                                    accountRepo.findByEmail(email2)
                                            .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + email2)))
                            )
                            .flatMap(tuple -> { //order (smaller_id, bigger_id)
                                Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                                Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());

                                return friendRepo.existsByUserId1AndUserId2(id1, id2)
                                        .flatMap(exists -> exists
                                                ? Mono.error(new IllegalStateException("Users are already friends")) //true
                                                : friendRepo.save(new Friend(id1, id2))            //false
                                                    .thenReturn(new ApiResponseDTO(true))
                                        );
                            });
                })
                .onErrorResume(e -> Mono.just(new ApiResponseDTO(
                        false,
                        e.getMessage(),
                        e instanceof IllegalStateException ? 400 : 404
                )));
    }

    @Override
    public Mono<ApiResponseDTO> removeFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exactly 2 friends required")))
                .flatMap(friends -> {
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

                                return friendRepo.existsByUserId1AndUserId2(id1, id2)
                                        .flatMap(exists -> exists
                                                ? friendRepo.deleteByUserId1AndUserId2(id1, id2)
                                                .thenReturn(new ApiResponseDTO(true))
                                                : Mono.error(new IllegalStateException("Users are not friends"))
                                        );
                            });
                })
                .onErrorResume(e -> Mono.just(new ApiResponseDTO(
                        false,
                        e.getMessage(),
                        e instanceof IllegalStateException ? 400 : 404
                )));
    }


    public Mono<ApiResponseDTO> getFriends(AccountDTO dto) {
        return accountRepo.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + dto.getEmail())))
                .flatMap(account ->
                        friendRepo.findAllByUserId1OrUserId2(account.getUser_id(), account.getUser_id()) //Flux<>
                                .flatMap(friend -> {
                                    // Select all row (id1, id2) in Friend where account.getId = id1 || id2
                                    Integer friendId = friend.getUserId1().equals(account.getUser_id())
                                            ? friend.getUserId2()  //In (id1, id2), if account.getId = id1 -> friend = id2
                                            : friend.getUserId1(); //else friend = id1
                                    return accountRepo.findById(friendId)
                                            .map(friendAccount -> friendAccount.getEmail()); //Account::getEmail
                                })
                                .collectList() //Flux<String> to Mono<List<String>>
                                .map(friendsList -> new ApiResponseDTO(true, friendsList, friendsList.size()))
                )
                .onErrorResume(e -> Mono.just(
                        new ApiResponseDTO(false, e.getMessage(), 404)
                ));
    }

}
