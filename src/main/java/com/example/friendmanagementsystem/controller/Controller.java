package com.example.friendmanagementsystem.controller;

import com.example.friendmanagementsystem.dto.AccountDTO;
import com.example.friendmanagementsystem.dto.ApiResponseDTO;
import com.example.friendmanagementsystem.dto.UserConnectionDTO;
import com.example.friendmanagementsystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/account")
public class Controller {
    @Autowired
    private AccountService service;

    @GetMapping("/all")
    public Mono<List<String>> getAllEmails() {
        return service.getAllEmails();
    }

    // Add friend
    @PostMapping
    public Mono<ApiResponseDTO> addFriend(@RequestBody AccountDTO dto) {
        return service.addFriend(dto);
    }
    @DeleteMapping
    public Mono<ApiResponseDTO> unFriend(@RequestBody AccountDTO dto) {
        return service.removeFriend(dto);
    }

    // Get friend list
    @PostMapping("/friends")
    public Mono<ApiResponseDTO> getFriends(@RequestBody AccountDTO dto) {
        return service.getFriends(dto);
    }

    // Get common friends
    @PostMapping("/friend/mutual")
    public Mono<ApiResponseDTO> getMutualFriends(@RequestBody AccountDTO dto) {
        return service.getCommonFriends(dto);
    }

    // Follow
    @PostMapping("/follow")
    public Mono<ApiResponseDTO> subscribeUpdates(@RequestBody UserConnectionDTO dto) {
        return service.subscribeUpdates(dto);
    }
    @DeleteMapping("/follow")
    public Mono<ApiResponseDTO> unsubscribeUpdates(@RequestBody UserConnectionDTO dto) {
        return service.unsubscribeUpdates(dto);
    }

    // Block
    @PostMapping("/block")
    public Mono<ApiResponseDTO> blockUpdates(@RequestBody UserConnectionDTO dto) {
        return service.blockUpdates(dto);
    }
    @DeleteMapping("/block")
    public Mono<ApiResponseDTO> unblockUpdates(@RequestBody UserConnectionDTO dto) {
        return service.unblockUpdates(dto);
    }


}