package com.example.emps.dto;

import com.example.emps.enums.TicketCategory;
import com.example.emps.enums.TicketPriority;
import com.example.emps.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SupportTicketDto {

    private Long id;

    private String ticketNumber;

    private String subject;

    private TicketCategory category;

    private TicketPriority priority;

    private TicketStatus status;

    private String description;

    private Long createdById;

    private String createdByName;

    private Long assignedToId;

    private String assignedToName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime assignedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;

    private List<SupportTicketCommentDto> comments;

    private List<SupportTicketAttachmentDto> attachments;

    private List<SupportTicketHistoryDto> history;
}