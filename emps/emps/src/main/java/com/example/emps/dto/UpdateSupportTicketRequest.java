package com.example.emps.dto;

import com.example.emps.enums.TicketPriority;
import com.example.emps.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSupportTicketRequest {

    private TicketStatus status;

    private TicketPriority priority;

    private Long assignedToId;
}