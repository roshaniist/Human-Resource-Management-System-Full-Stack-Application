package com.example.emps.controller;

import com.example.emps.dto.CreateSupportTicketRequest;
import com.example.emps.dto.SupportTicketAttachmentDto;
import com.example.emps.dto.SupportTicketCommentDto;
import com.example.emps.dto.SupportTicketDto;
import com.example.emps.dto.UpdateSupportTicketRequest;
import com.example.emps.service.SupportTicketAttachmentService;
import com.example.emps.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;
    private final SupportTicketAttachmentService supportTicketAttachmentService;


    // =========================================================
    // CREATE TICKET
    // =========================================================

    /*
     * EMPLOYEE / MANAGER / HR can create tickets.
     *
     * ADMIN and IT_SUPPORT cannot create tickets.
     *
     * Backend service also validates this permission.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<SupportTicketDto> createTicket(
            @Valid @RequestBody CreateSupportTicketRequest request) {

        return ResponseEntity.ok(
                supportTicketService.createTicket(request)
        );
    }


    // =========================================================
    // GET MY TICKETS
    // =========================================================

    /*
     * Returns tickets created by current logged-in user.
     */
    @GetMapping("/my-tickets")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<SupportTicketDto>> getMyTickets() {

        return ResponseEntity.ok(
                supportTicketService.getMyTickets()
        );
    }


    // =========================================================
    // GET ASSIGNED TICKETS - IT SUPPORT
    // =========================================================

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<List<SupportTicketDto>> getAssignedTickets() {

        return ResponseEntity.ok(
                supportTicketService.getAssignedTickets()
        );
    }


    // =========================================================
    // GET ALL TICKETS - ADMIN
    // =========================================================

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketDto>> getAllTickets() {

        return ResponseEntity.ok(
                supportTicketService.getAllTickets()
        );
    }


    // =========================================================
    // GET TICKET BY ID
    // =========================================================

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<SupportTicketDto> getTicketById(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                supportTicketService.getTicketById(ticketId)
        );
    }


    // =========================================================
    // ASSIGN TICKET - ADMIN ONLY
    // =========================================================

    @PutMapping("/{ticketId}/assign/{assignedToId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportTicketDto> assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long assignedToId) {

        return ResponseEntity.ok(
                supportTicketService.assignTicket(
                        ticketId,
                        assignedToId
                )
        );
    }


    // =========================================================
    // UPDATE TICKET
    // =========================================================

    /*
     * ADMIN:
     *   Can assign IT Support.
     *   Cannot change priority/status.
     *
     * IT_SUPPORT:
     *   Can update status of assigned ticket.
     *
     * Priority is immutable and validated in service.
     */
    @PutMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'IT_SUPPORT')")
    public ResponseEntity<SupportTicketDto> updateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateSupportTicketRequest request) {

        return ResponseEntity.ok(
                supportTicketService.updateTicket(
                        ticketId,
                        request
                )
        );
    }


    // =========================================================
    // ADD COMMENT
    // =========================================================

    @PostMapping("/{ticketId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<SupportTicketCommentDto> addComment(
            @PathVariable Long ticketId,
            @RequestParam String comment) {

        return ResponseEntity.ok(
                supportTicketService.addComment(
                        ticketId,
                        comment
                )
        );
    }


    // =========================================================
    // RESOLVE TICKET - IT SUPPORT ONLY
    // =========================================================

    /*
     * Only assigned IT Support can resolve a ticket.
     *
     * ADMIN cannot resolve.
     */
    @PutMapping("/{ticketId}/resolve")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<SupportTicketDto> resolveTicket(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                supportTicketService.resolveTicket(ticketId)
        );
    }


    // =========================================================
    // CLOSE TICKET - REQUESTER CONFIRMATION
    // =========================================================

    /*
     * Employee / Manager / HR can confirm
     * their own RESOLVED ticket.
     *
     * ADMIN and IT_SUPPORT cannot close directly.
     */
    @PutMapping("/{ticketId}/close")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<SupportTicketDto> closeTicket(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                supportTicketService.closeTicket(ticketId)
        );
    }


    // =========================================================
    // REOPEN TICKET
    // =========================================================

    /*
     * Reopen is currently disabled in the service layer.
     *
     * If reopen is enabled in future, permissions
     * can be updated here.
     */
    @PutMapping("/{ticketId}/reopen")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<SupportTicketDto> reopenTicket(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                supportTicketService.reopenTicket(ticketId)
        );
    }


    // =========================================================
    // UPLOAD ATTACHMENT
    // =========================================================

    @PostMapping(
            value = "/{ticketId}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<SupportTicketAttachmentDto> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                supportTicketAttachmentService.uploadAttachment(
                        ticketId,
                        file
                )
        );
    }


    // =========================================================
    // GET ATTACHMENTS
    // =========================================================

    @GetMapping("/{ticketId}/attachments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<SupportTicketAttachmentDto>> getTicketAttachments(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                supportTicketAttachmentService.getTicketAttachments(
                        ticketId
                )
        );
    }


    // =========================================================
    // DOWNLOAD ATTACHMENT
    // =========================================================

    @GetMapping("/attachments/{attachmentId}/file")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<Resource> getAttachmentFile(
            @PathVariable Long attachmentId) {

        Resource resource =
                supportTicketAttachmentService.getAttachmentFile(
                        attachmentId
                );

        String contentType =
                "application/octet-stream";

        try {

            if (resource.getFilename() != null) {

                String filename =
                        resource.getFilename();

                if (filename.toLowerCase().endsWith(".pdf")) {

                    contentType =
                            "application/pdf";

                } else if (filename.toLowerCase().endsWith(".png")) {

                    contentType =
                            "image/png";

                } else if (filename.toLowerCase().endsWith(".jpg") ||
                        filename.toLowerCase().endsWith(".jpeg")) {

                    contentType =
                            "image/jpeg";

                } else if (filename.toLowerCase().endsWith(".txt")) {

                    contentType =
                            "text/plain";

                } else if (filename.toLowerCase().endsWith(".doc")) {

                    contentType =
                            "application/msword";

                } else if (filename.toLowerCase().endsWith(".docx")) {

                    contentType =
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

                } else if (filename.toLowerCase().endsWith(".xls")) {

                    contentType =
                            "application/vnd.ms-excel";

                } else if (filename.toLowerCase().endsWith(".xlsx")) {

                    contentType =
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                }
            }

        } catch (Exception ignored) {
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() +
                                "\""
                )
                .body(resource);
    }


    // =========================================================
    // DELETE ATTACHMENT - ADMIN ONLY
    // =========================================================

    @DeleteMapping("/attachments/{attachmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long attachmentId) {

        supportTicketAttachmentService.deleteAttachment(
                attachmentId
        );

        return ResponseEntity.noContent().build();
    }
}