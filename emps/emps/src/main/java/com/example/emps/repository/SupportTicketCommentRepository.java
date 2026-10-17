package com.example.emps.repository;

import com.example.emps.entity.SupportTicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketCommentRepository extends JpaRepository<SupportTicketComment, Long> {

    List<SupportTicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}