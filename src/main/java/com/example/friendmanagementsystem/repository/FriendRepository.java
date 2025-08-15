package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Friend;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FriendRepository extends ReactiveCrudRepository<Friend, Integer>{
    Mono<Boolean> existsByUserId1AndUserId2(Integer userId1, Integer userId2);
    Mono<Void> deleteByUserId1AndUserId2(Integer userId1, Integer userId2);

    //SELECT * FROM friend WHERE user_id1 = :userId1 OR user_id2 = :userId2
    Flux<Friend> findAllByUserId1OrUserId2(Integer userId1, Integer userId2);

}
