package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.common.exception.AppException;
import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private FriendRepository friendRepo;


    @Override
    public Mono<ApiResponseDTO> addFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list != null && list.size() == 2) // Must have exactly 2 emails
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .filter(list -> !list.getFirst().equalsIgnoreCase(list.get(1))) // Emails must not be the same
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.SAME_EMAILS)))
                .flatMap(validFriends -> Mono.zip( // Subscribes in parallel, waits for both to complete
                        accountRepo.findByEmail(validFriends.getFirst())
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                        accountRepo.findByEmail(validFriends.get(1))
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                ))
                .flatMap(tuple -> { // order (smaller_id, bigger_id)
                    Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                    Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());

                    return friendRepo.existsByUserId1AndUserId2(id1, id2)
                            .flatMap(exists -> exists
                                    ? Mono.error(new AppException(ErrorCode.ALREADY_FRIENDS)) // true
                                    : friendRepo.save(new Friend(id1, id2)) // false
                                    .thenReturn(new ApiResponseDTO(true))
                            );
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
                .filter(list -> !list.getFirst().equalsIgnoreCase(list.get(1)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.SAME_EMAILS)))
                .flatMap(friends -> Mono.zip(
                        accountRepo.findByEmail(friends.getFirst())
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                        accountRepo.findByEmail(friends.get(1))
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                ))
                .flatMap(tuple -> {
                    Integer id1 = Math.min(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());
                    Integer id2 = Math.max(tuple.getT1().getUser_id(), tuple.getT2().getUser_id());

                    return friendRepo.existsByUserId1AndUserId2(id1, id2)
                            .flatMap(exists -> exists
                                    ? friendRepo.deleteByUserId1AndUserId2(id1, id2)
                                    .thenReturn(new ApiResponseDTO(true))
                                    : Mono.error(new AppException(ErrorCode.NOT_FRIENDS))
                            );
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }

    @Override
    public Mono<ApiResponseDTO> getFriends(AccountDTO dto) {
        return accountRepo.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                .flatMap(account ->
                        friendRepo.findAllFriendIdsByUserId(account.getUser_id()) // get all friend ids
                                .collectList() // Mono<List<Integer>>
                                .flatMap(friendIds -> {
                                    if (friendIds.isEmpty()) {
                                        return Mono.just(new ApiResponseDTO(true, List.of(), 0));
                                    }
                                    return accountRepo.findAllById(friendIds) // fetch all accounts at once
                                            .map(Account::getEmail)
                                            .collectList()
                                            .map(friendsList -> new ApiResponseDTO(true, friendsList, friendsList.size()));
                                })
                )
                .onErrorResume(AppException.class, e -> Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }

    @Override
    public Mono<ApiResponseDTO> getCommonFriends(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .filter(list -> !list.getFirst().equalsIgnoreCase(list.get(1)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.SAME_EMAILS)))
                .flatMap(friends -> Mono.zip(
                        accountRepo.findByEmail(friends.getFirst())
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                        accountRepo.findByEmail(friends.get(1))
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                ))
                .flatMap(tuple -> Mono.zip( // get friends of both users
                        friendRepo.findAllFriendIdsByUserId(tuple.getT1().getUser_id()).collect(Collectors.toSet()),
                        friendRepo.findAllFriendIdsByUserId(tuple.getT2().getUser_id()).collect(Collectors.toSet())
                ))
                .flatMap(pair -> { // return sets of friends: Set.of(id2, id3, id4)
                    pair.getT1().retainAll(pair.getT2()); // pair.getT1() = Retain only ids in set pair.getT2()
                    return pair.getT1().isEmpty()
                            ? Mono.just(new ApiResponseDTO(true, List.of(), 0))
                            : accountRepo.findAllById(pair.getT1())
                            .map(Account::getEmail)
                            .collectList()
                            .map(list -> new ApiResponseDTO(true, list, list.size()));
                })
                .onErrorResume(AppException.class, e -> Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }

}

