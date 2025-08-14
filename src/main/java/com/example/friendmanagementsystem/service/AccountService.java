package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountDTO> addFriend(AccountDTO dto);
}
