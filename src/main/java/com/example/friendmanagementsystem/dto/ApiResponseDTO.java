package com.example.friendmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private boolean success;
    private String message;
    private Integer statusCode;


    public ApiResponseDTO(boolean success) {
        this.success = success;
    }

}