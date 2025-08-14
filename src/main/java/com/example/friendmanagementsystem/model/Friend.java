package com.example.friendmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("friend")
public class Friend {
    @Column("user_id1")
    private Integer userId1;

    @Column("user_id2")
    private Integer userId2;

}