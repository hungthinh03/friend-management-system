package com.example.friendmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("block")
public class Block {
    @Column("blocker_id")
    private Integer blockerId;

    @Column("blocked_id")
    private Integer blockedId;

}