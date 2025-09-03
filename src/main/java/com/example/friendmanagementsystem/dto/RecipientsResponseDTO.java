package com.example.friendmanagementsystem.dto;

import com.example.friendmanagementsystem.common.enums.ErrorCode;
import lombok.Data;

import java.util.List;

@Data
public class RecipientsResponseDTO extends ApiResponseDTO{
    private List<String> recipients;

    public RecipientsResponseDTO(boolean success, List<String> recipients) { //success find all recipients
        super(success);
        this.recipients = recipients;
    }

    public RecipientsResponseDTO(ErrorCode errorCode) {
        super(errorCode);
    }
}
