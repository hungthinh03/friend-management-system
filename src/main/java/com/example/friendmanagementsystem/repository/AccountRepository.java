package com.example.friendmanagementsystem.repository;

import com.example.friendmanagementsystem.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Integer>{
    Mono<Account> findByEmail(String email1);

    @Query("SELECT email FROM account")
    Flux<String> findAllEmails();

    Mono<Void> deleteAccountByEmail(String email);
}
