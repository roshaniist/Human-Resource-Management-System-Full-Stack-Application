package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SupportTicketAttachmentDto {

    private Long id;

    private String fileName;

    private String fileType;

    private String uploadedByName;

    private LocalDateTime uploadedAt;
}