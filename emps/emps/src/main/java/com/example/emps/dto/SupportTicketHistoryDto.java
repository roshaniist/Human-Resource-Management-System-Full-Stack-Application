package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SupportTicketHistoryDto {

    private Long id;

    private String changedByName;

    private String action;

    private String oldValue;

    private String newValue;

    private LocalDateTime changedAt;
}