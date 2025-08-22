package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.example.friendmanagementsystem.common.exception.AppException;
import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.dto.PostDTO;
import com.example.friendmanagementsystem.dto.UserConnectionDTO;
import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Block;
import com.example.friendmanagementsystem.model.Follower;
import com.example.friendmanagementsystem.model.Friend;
import com.example.friendmanagementsystem.repository.AccountRepository;
import com.example.friendmanagementsystem.repository.BlockRepository;
import com.example.friendmanagementsystem.repository.FollowerRepository;
import com.example.friendmanagementsystem.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private FriendRepository friendRepo;
    @Autowired
    private FollowerRepository followerRepo;
    @Autowired
    private BlockRepository blockRepo;

    @Override
    public Mono<List<String>> getAllEmails() {
        return accountRepo.findAllEmails().collectList();
    }

    // Helper
    private Mono<Tuple2<Account, Account>> validateAndFetchAccounts(String email1, String email2) {
        if (email1.equalsIgnoreCase(email2)) {
            return Mono.error(new AppException(ErrorCode.SAME_EMAILS)); // Emails must not be the same
        }
        return Mono.zip(
                accountRepo.findByEmail(email1)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND))),
                accountRepo.findByEmail(email2)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
        );
    }

    private Mono<Void> validateNotBlocked(Integer userId1, Integer userId2) {
        return Mono.zip( // Check if exists block (id1,id2) or (id2,id1)
                        blockRepo.existsByBlockerIdAndBlockedId(userId1, userId2),
                        blockRepo.existsByBlockerIdAndBlockedId(userId2, userId1)
                )
                .map(tuple -> tuple.getT1() || tuple.getT2())
                .filter(exists -> !exists) // exists = true if either block exists
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_CONNECTION_BLOCKED)))
                .then(); // returns void instead of Mono<Boolean>
    }

    @Override
    public Mono<ApiResponseDTO> addFriend(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list != null && list.size() == 2) // Must have exactly 2 emails
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .flatMap(list -> validateAndFetchAccounts(list.getFirst(), list.get(1))) // Helper
                .flatMap(tuple -> { // order (smaller_id, bigger_id)
                    Integer id1 = Math.min(tuple.getT1().getUserId(), tuple.getT2().getUserId());
                    Integer id2 = Math.max(tuple.getT1().getUserId(), tuple.getT2().getUserId());

                    return validateNotBlocked(id1, id2) // if not block -> proceed
                            .then(
                                    friendRepo.existsByUserId1AndUserId2(id1, id2)
                                        .flatMap(exists -> exists
                                                ? Mono.error(new AppException(ErrorCode.ALREADY_FRIENDS)) // true
                                                : friendRepo.save(new Friend(id1, id2)) // false
                                                .thenReturn(new ApiResponseDTO(true))
                                        )
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
                .flatMap(list -> validateAndFetchAccounts(list.getFirst(), list.get(1)))
                .flatMap(tuple -> {
                    Integer id1 = Math.min(tuple.getT1().getUserId(), tuple.getT2().getUserId());
                    Integer id2 = Math.max(tuple.getT1().getUserId(), tuple.getT2().getUserId());

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
                        friendRepo.findAllFriendIdsByUserId(account.getUserId()) // get all friend ids
                                .collectList() // Mono<List<Integer>>
                                .flatMap(friendIds -> {
                                    if (friendIds.isEmpty()) {
                                        return Mono.just(new ApiResponseDTO(true, List.of(), 0));
                                    }
                                    return accountRepo.findAllById(friendIds) // fetch all accounts at once
                                            .map(Account::getEmail)
                                            .collectList()
                                            .map(friendsList ->
                                                    new ApiResponseDTO(true, friendsList, friendsList.size()));
                                })
                )
                .onErrorResume(AppException.class, e -> Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }

    @Override
    public Mono<ApiResponseDTO> getCommonFriends(AccountDTO dto) {
        return Mono.justOrEmpty(dto.getFriends())
                .filter(list -> list.size() == 2)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .flatMap(list -> validateAndFetchAccounts(list.getFirst(), list.get(1)))
                .flatMap(tuple -> Mono.zip( // get friends of both users
                        friendRepo.findAllFriendIdsByUserId(tuple.getT1().getUserId()).collect(Collectors.toSet()),
                        friendRepo.findAllFriendIdsByUserId(tuple.getT2().getUserId()).collect(Collectors.toSet())
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


    @Override
    public Mono<ApiResponseDTO> subscribeUpdates(UserConnectionDTO dto) {
        return validateAndFetchAccounts(dto.getRequestor(), dto.getTarget())
                .flatMap(tuple -> {
                    Integer followerId = tuple.getT1().getUserId();
                    Integer followeeId = tuple.getT2().getUserId();

                    return validateNotBlocked(followerId, followeeId) // if not block -> proceed
                            .then(
                                    followerRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)
                                            .flatMap(exists -> exists
                                                    ? Mono.error(new AppException(ErrorCode.ALREADY_FOLLOWED))
                                                    : followerRepo.save(new Follower(followerId, followeeId))
                                                    .thenReturn(new ApiResponseDTO(true))
                                            )
                            );
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    @Override
    public Mono<ApiResponseDTO> unsubscribeUpdates(UserConnectionDTO dto) {
        return validateAndFetchAccounts(dto.getRequestor(), dto.getTarget())
                .flatMap(tuple -> {
                    Integer followerId = tuple.getT1().getUserId();
                    Integer followeeId = tuple.getT2().getUserId();

                    return followerRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)
                            .flatMap(exists -> exists
                                    ? followerRepo.deleteByFollowerIdAndFolloweeId(followerId, followeeId)
                                    .thenReturn(new ApiResponseDTO(true))
                                    : Mono.error(new AppException(ErrorCode.NOT_FOLLOWED))
                            );
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }


    @Override
    public Mono<ApiResponseDTO> blockUpdates(UserConnectionDTO dto) {
        return validateAndFetchAccounts(dto.getRequestor(), dto.getTarget())
                .flatMap(tuple -> {
                    Integer blockerId = tuple.getT1().getUserId();
                    Integer blockedId = tuple.getT2().getUserId();

                    return blockRepo.existsByBlockerIdAndBlockedId(blockerId, blockedId)
                            .flatMap(exists -> exists
                                    ? Mono.error(new AppException(ErrorCode.ALREADY_BLOCKED))
                                    : blockRepo.save(new Block(blockerId, blockedId))
                                    .then(
                                            Mono.when(  // remove follow both ways
                                                    followerRepo.deleteByFollowerIdAndFolloweeId(blockerId, blockedId),
                                                    followerRepo.deleteByFollowerIdAndFolloweeId(blockedId, blockerId)
                                            )
                                    )
                                    .thenReturn(new ApiResponseDTO(true))
                            );
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    @Override
    public Mono<ApiResponseDTO> unblockUpdates(UserConnectionDTO dto) {
        return validateAndFetchAccounts(dto.getRequestor(), dto.getTarget())
                .flatMap(tuple -> {
                    Integer blockerId = tuple.getT1().getUserId();
                    Integer blockedId = tuple.getT2().getUserId();

                    return blockRepo.existsByBlockerIdAndBlockedId(blockerId, blockedId)
                            .flatMap(exists -> exists
                                    ? blockRepo.deleteByBlockerIdAndBlockedId(blockerId, blockedId)
                                    .thenReturn(new ApiResponseDTO(true))
                                    : Mono.error(new AppException(ErrorCode.NOT_BLOCKED))
                            );
                })
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    private Mono<Boolean> hasNoBlockConnection(Integer userId1, Integer userId2) {
        return Mono.zip(
                        blockRepo.existsByBlockerIdAndBlockedId(userId1, userId2),
                        blockRepo.existsByBlockerIdAndBlockedId(userId2, userId1)
                )
                .map(tuple -> !(tuple.getT1() || tuple.getT2())); // true if neither has blocked
    }

    @Override
    public Mono<ApiResponseDTO> getUpdateRecipients(PostDTO dto) {
        return Mono.just(dto)
                .filter(d -> d.getText() != null && !d.getText().isBlank())
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_REQUEST)))
                .flatMap(validDto -> accountRepo.findByEmail(validDto.getSender())
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_FOUND)))
                        .flatMap(sender -> {
                            Integer senderId = sender.getUserId();
                            String senderEmail = sender.getEmail();

                            return Flux.merge(
                                            friendRepo.findAllFriendIdsByUserId(senderId).flatMap(accountRepo::findById),
                                            followerRepo.findByFolloweeId(senderId).flatMap(accountRepo::findById),
                                            Flux.fromIterable(validDto.extractEmails()).flatMap(accountRepo::findByEmail)
                                    )
                                    .distinct()
                                    .filterWhen(acc -> hasNoBlockConnection(senderId, acc.getUserId()))
                                    .filter(acc -> !acc.getEmail().equalsIgnoreCase(senderEmail))
                                    .map(Account::getEmail)
                                    .collectList()
                                    .map(recipients -> new ApiResponseDTO(true, recipients));
                        }))
                .onErrorResume(AppException.class, e -> Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }
}

