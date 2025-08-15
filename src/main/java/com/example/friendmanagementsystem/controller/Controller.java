package com.example.friendmanagementsystem.controller;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class Controller {
    @Autowired
    private AccountService service;

    // Create
    @PostMapping
    public Mono<ApiResponseDTO> addFriend(@RequestBody AccountDTO dto) {
        return service.addFriend(dto);
    }

    // Read
    @PostMapping("/friends")
    public Mono<ApiResponseDTO> getFriends(@RequestBody AccountDTO dto) {
        return service.getFriends(dto);
    }


    // Delete
    @DeleteMapping
    public Mono<ApiResponseDTO> unFriend(@RequestBody AccountDTO dto) {
        return service.removeFriend(dto);
    }

}