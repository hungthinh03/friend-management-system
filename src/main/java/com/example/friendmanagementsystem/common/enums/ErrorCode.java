package com.example.friendmanagementsystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(1001, "Invalid request"),
    USER_NOT_FOUND(1002, "User not found"),
    ALREADY_FRIENDS(1003, "Users are already friends"),
    NOT_FRIENDS(1004, "Users are not friends"),
    ALREADY_FOLLOWED(1005, "User is already followed"),
    NOT_FOLLOWED(1006, "User is not followed"),
    ALREADY_BLOCKED(1007, "User is already blocked"),
    NOT_BLOCKED(1008, "User is not blocked"),
    SAME_EMAILS(1009, "Emails must be different"),
    UNEXPECTED_ERROR(1999, "Unexpected error");

    private final int code;
    private final String message;
}

