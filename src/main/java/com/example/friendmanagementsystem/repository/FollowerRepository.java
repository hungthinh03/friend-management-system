package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Follower;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FollowerRepository extends ReactiveCrudRepository<Follower, Integer> {

    Mono<Boolean> existsByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    Mono<Void>  deleteByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId);

    Flux<Integer> findByFolloweeId(Integer senderId);
}
