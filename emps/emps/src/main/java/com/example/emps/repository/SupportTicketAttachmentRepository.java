package com.example.emps.repository;

import com.example.emps.entity.SupportTicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketAttachmentRepository extends JpaRepository<SupportTicketAttachment, Long> {

    List<SupportTicketAttachment> findByTicketIdOrderByUploadedAtAsc(Long ticketId);
}