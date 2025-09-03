package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {
    Mono<List<String>>  getAllEmails();

    Mono<ApiResponseDTO> deleteAccount(AccountDTO dto);

    //
    Mono<ApiResponseDTO> addFriend(AccountDTO dto);
    Mono<ApiResponseDTO> removeFriend(AccountDTO dto);
    //
    Mono<FriendsResponseDTO> getFriends(AccountDTO dto);
    //
    Mono<FriendsResponseDTO> getCommonFriends(AccountDTO dto);
    //
    Mono<ApiResponseDTO> subscribeUpdates(UserConnectionDTO dto);
    Mono<ApiResponseDTO> unsubscribeUpdates(UserConnectionDTO dto);
    //
    Mono<ApiResponseDTO> blockUpdates(UserConnectionDTO dto);
    Mono<ApiResponseDTO> unblockUpdates(UserConnectionDTO dto);
    //
    Mono<RecipientsResponseDTO> getUpdateRecipients(PostDTO dto);
}
