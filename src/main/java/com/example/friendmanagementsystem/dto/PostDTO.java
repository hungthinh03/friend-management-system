package com.example.friendmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String sender;
    private String text;

    public List<String> extractEmails() {
        if (text == null || text.isEmpty()) return List.of();

        // simple regex to find emails
        Pattern emailPattern = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        );
        Matcher matcher = emailPattern.matcher(text);

        List<String> emails = new ArrayList<>();
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        return emails;
    }
}
