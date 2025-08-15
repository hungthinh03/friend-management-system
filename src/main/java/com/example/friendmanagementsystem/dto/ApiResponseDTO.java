package com.example.friendmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private boolean success;
    private String message;
    private Integer statusCode;
    private List<String> friends;
    private Integer count;


    public ApiResponseDTO(boolean success) {
        this.success = success;
    }

    public ApiResponseDTO(boolean success, String message, Integer statusCode) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ApiResponseDTO(boolean success, List<String> friends, Integer count) {
        this.success = success;
        this.friends = friends;
        this.count = count;
    }
}