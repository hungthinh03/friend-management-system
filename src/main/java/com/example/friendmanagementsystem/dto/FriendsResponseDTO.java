package com.example.friendmanagementsystem.dto;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import lombok.Data;

import java.util.List;

@Data
public class FriendsResponseDTO extends ApiResponseDTO {
    private List<String> friends;
    private Integer count;

    public FriendsResponseDTO(boolean success, List<String> friends, Integer count) {
        super(success);
        this.friends = friends;
        this.count = count;
    }

    public FriendsResponseDTO(ErrorCode errorCode) {
        super(errorCode);
    }
}
