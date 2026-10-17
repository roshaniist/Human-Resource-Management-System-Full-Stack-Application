package com.example.emps.service;

import com.example.emps.dto.SupportTicketAttachmentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SupportTicketAttachmentService {

    SupportTicketAttachmentDto uploadAttachment(
            Long ticketId,
            MultipartFile file
    );

    List<SupportTicketAttachmentDto> getTicketAttachments(
            Long ticketId
    );

    Resource getAttachmentFile(
            Long attachmentId
    );

    void deleteAttachment(
            Long attachmentId
    );
}