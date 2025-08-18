package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.common.exception.AppException;
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
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .flatMap(friends -> {
                    String email1 = friends.get(0);
                    String email2 = friends.get(1);

                    return Mono.zip( //Subscribes in parallel, waits for both to complete
                                    accountRepo.findByEmail(email1)
                                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                                    accountRepo.findByEmail(email2)
                                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                            )
                            .flatMap(tuple -> { //order (smaller_id, bigger_id)
                                Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                                Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());

                                return friendRepo.existsByUserId1AndUserId2(id1, id2)
                                        .flatMap(exists -> exists
                                                ? Mono.error(new AppException(ErrorCode.ALREADY_FRIENDS)) //true
                                                : friendRepo.save(new Friend(id1, id2))            //false
                                                    .thenReturn(new ApiResponseDTO(true))
                                        );
                            });
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    @Override
    public Mono<ApiResponseDTO> removeFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .flatMap(friends -> {
                    String email1 = friends.get(0);
                    String email2 = friends.get(1);

                    return Mono.zip(
                                    accountRepo.findByEmail(email1)
                                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                                    accountRepo.findByEmail(email2)
                                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                            )
                            .flatMap(tuple -> {
                                Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                                Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());

                                return friendRepo.existsByUserId1AndUserId2(id1, id2)
                                        .flatMap(exists -> exists
                                                ? friendRepo.deleteByUserId1AndUserId2(id1, id2)
                                                .thenReturn(new ApiResponseDTO(true))
                                                : Mono.error(new AppException(ErrorCode.NOT_FRIENDS))
                                        );
                            });
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }


    public Mono<ApiResponseDTO> getFriends(AccountDTO dto) {
        return accountRepo.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
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
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );

    /*
    @Override
    public Mono<ApiResponseDTO> getCommonFriends(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exactly 2 friends required")))
                .flatMap(friends -> Mono.zip(
                        accountRepo.findByEmail(friends.get(0))
                                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + friends.get(0)))),
                        accountRepo.findByEmail(friends.get(1))
                                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + friends.get(1))))
                ))
                .flatMap(tuple -> {
                    Integer id1 = tuple.getT1().getUser_id();
                    Integer id2 = tuple.getT2().getUser_id();

                    // Get friends of both users
                    Mono<Set<Integer>> set1 = friendRepo.findAllFriendIdsByUserId(id1)
                            .collect(Collectors.toSet());
                    Mono<Set<Integer>> set2 = friendRepo.findAllFriendIdsByUserId(id2)
                            .collect(Collectors.toSet());

                    return Mono.zip(set1, set2)
                            .flatMap(pair -> {
                                // Intersection = common friends
                                pair.getT1().retainAll(pair.getT2());
                                Set<Integer> commonIds = pair.getT1();

                                if (commonIds.isEmpty()) {
                                    return Mono.just(new ApiResponseDTO(true, List.of(), 0));
                                }

                                // return emails of common friends
                                return accountRepo.findAllById(commonIds)
                                        .map(Account::getEmail)
                                        .collectList()
                                        .map(friendsList -> new ApiResponseDTO(true, friendsList, emails.size()));
                            });
                })
                .onErrorResume(e -> Mono.just(new ApiResponseDTO("error", e.getMessage(), 404)));
    */
    }



}
