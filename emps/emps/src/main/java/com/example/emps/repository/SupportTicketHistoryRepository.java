package com.example.emps.repository;

import com.example.emps.entity.SupportTicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketHistoryRepository extends JpaRepository<SupportTicketHistory, Long> {

    List<SupportTicketHistory> findByTicketIdOrderByChangedAtAsc(Long ticketId);
}