package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Friend;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FriendRepository extends ReactiveCrudRepository<Friend, Integer>{
    Mono<Boolean> existsByUserId1AndUserId2(Integer userId1, Integer userId2);
}
