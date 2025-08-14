package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Account;
import com.example.friendmanagementsystem.model.Friend;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends ReactiveCrudRepository<Friend, Integer>{
}
