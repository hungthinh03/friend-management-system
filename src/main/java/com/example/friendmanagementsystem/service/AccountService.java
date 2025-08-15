package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<ApiResponseDTO> addFriend(AccountDTO dto);
}
