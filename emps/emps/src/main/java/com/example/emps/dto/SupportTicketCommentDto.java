package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SupportTicketCommentDto {

    private Long id;

    private Long userId;

    private String userName;

    private String comment;

    private LocalDateTime createdAt;
}