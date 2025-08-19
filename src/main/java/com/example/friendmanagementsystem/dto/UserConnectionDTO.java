package com.example.friendmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserConnectionDTO {
    private String requestor;
    private String target;
}