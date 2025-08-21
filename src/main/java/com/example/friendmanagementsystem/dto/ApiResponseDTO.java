package com.example.friendmanagementsystem.dto;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO {
    private Boolean success;
    private String status;
    private ErrorCode error;
    private String message;
    private Integer statusCode;
    private List<String> friends;
    private List<String> recipients;
    private Integer count;


    public ApiResponseDTO(boolean success) {
        this.success = success;
    }

    public ApiResponseDTO(ErrorCode errorCode) {
        this.status = "error";
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getCode();
    }

    public ApiResponseDTO(boolean success, List<String> friends, Integer count) {
        this.success = success;
        this.friends = friends;
        this.count = count;
    }

    public ApiResponseDTO(boolean success, List<String> recipients) {
        this.success = success;
        this.recipients = recipients;
    }
}