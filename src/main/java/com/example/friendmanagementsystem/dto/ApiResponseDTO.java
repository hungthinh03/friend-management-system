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

    public ApiResponseDTO(boolean success) { //standard success
        this.success = success;
    }

    public ApiResponseDTO(ErrorCode errorCode) { //error
        this.status = "error";
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getCode();
    }

}