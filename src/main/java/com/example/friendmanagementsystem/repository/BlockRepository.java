package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Block;
import com.example.friendmanagementsystem.model.Follower;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BlockRepository extends ReactiveCrudRepository<Block, Integer> {
    Mono<Boolean> existsByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);

    Mono<Void>deleteByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);
}
