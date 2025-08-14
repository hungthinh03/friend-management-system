package com.example.friendmanagementsystem.service;

import com.example.friendmanagementsystem.dto.AccountDTO;
import reactor.core.publisher.Mono;

public class AccountServiceImpl implements AccountService {
    @Override
    public Mono<AccountDTO> addFriend(AccountDTO dto) {



        return Mono.just(dto); //placeholder
    }

}
