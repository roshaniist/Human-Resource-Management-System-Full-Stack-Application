package com.example.emps.service;

import com.example.emps.dto.CreateSupportTicketRequest;
import com.example.emps.dto.SupportTicketCommentDto;
import com.example.emps.dto.SupportTicketDto;
import com.example.emps.dto.UpdateSupportTicketRequest;

import java.util.List;

public interface SupportTicketService {

    SupportTicketDto createTicket(CreateSupportTicketRequest request);

    SupportTicketDto getTicketById(Long ticketId);

    List<SupportTicketDto> getMyTickets();

    List<SupportTicketDto> getAllTickets();

    List<SupportTicketDto> getAssignedTickets();

    SupportTicketDto assignTicket(Long ticketId, Long assignedToId);

    SupportTicketDto updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request
    );

    SupportTicketCommentDto addComment(
            Long ticketId,
            String comment
    );

    SupportTicketDto resolveTicket(Long ticketId);

    SupportTicketDto closeTicket(Long ticketId);

    SupportTicketDto reopenTicket(Long ticketId);
}