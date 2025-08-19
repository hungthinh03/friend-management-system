package com.example.friendmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("follower")
public class Follower {
    @Column("follower_id")
    private Integer followerId; // person following

    @Column("followee_id")
    private Integer followeeId; // person being followed

}