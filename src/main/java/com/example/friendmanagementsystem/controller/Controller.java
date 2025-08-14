package com.example.friendmanagementsystem.controller;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class Controller {
    @Autowired
    private AccountService service;

    // Create
    @PostMapping
    public Mono<AccountDTO> addFriend(@RequestBody AccountDTO dto) {
        return service.addFriend(dto);
    }
}