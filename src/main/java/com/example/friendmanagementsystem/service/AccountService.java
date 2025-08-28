package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.dto.PostDTO;
import com.example.friendmanagementsystem.dto.UserConnectionDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {
    Mono<List<String>>  getAllEmails();

    Mono<ApiResponseDTO> deleteAccount(AccountDTO dto);

    //
    Mono<ApiResponseDTO> addFriend(AccountDTO dto);
    Mono<ApiResponseDTO> removeFriend(AccountDTO dto);
    //
    Mono<ApiResponseDTO> getFriends(AccountDTO dto);
    //
    Mono<ApiResponseDTO> getCommonFriends(AccountDTO dto);
    //
    Mono<ApiResponseDTO> subscribeUpdates(UserConnectionDTO dto);
    Mono<ApiResponseDTO> unsubscribeUpdates(UserConnectionDTO dto);
    //
    Mono<ApiResponseDTO> blockUpdates(UserConnectionDTO dto);
    Mono<ApiResponseDTO> unblockUpdates(UserConnectionDTO dto);
    //
    Mono<ApiResponseDTO> getUpdateRecipients(PostDTO dto);
}
