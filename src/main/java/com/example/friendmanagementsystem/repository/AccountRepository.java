package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Integer>{
}
