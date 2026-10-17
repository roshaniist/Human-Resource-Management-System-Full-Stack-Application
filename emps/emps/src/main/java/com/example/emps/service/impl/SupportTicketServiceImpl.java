package com.example.emps.service.impl;

import com.example.emps.dto.CreateSupportTicketRequest;
import com.example.emps.dto.SupportTicketAttachmentDto;
import com.example.emps.dto.SupportTicketCommentDto;
import com.example.emps.dto.SupportTicketDto;
import com.example.emps.dto.SupportTicketHistoryDto;
import com.example.emps.dto.UpdateSupportTicketRequest;
import com.example.emps.entity.SupportTicket;
import com.example.emps.entity.SupportTicketAttachment;
import com.example.emps.entity.SupportTicketComment;
import com.example.emps.entity.SupportTicketHistory;
import com.example.emps.entity.User;
import com.example.emps.enums.NotificationType;
import com.example.emps.enums.RoleType;
import com.example.emps.enums.TicketStatus;
import com.example.emps.repository.SupportTicketAttachmentRepository;
import com.example.emps.repository.SupportTicketCommentRepository;
import com.example.emps.repository.SupportTicketHistoryRepository;
import com.example.emps.repository.SupportTicketRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.NotificationService;
import com.example.emps.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketCommentRepository supportTicketCommentRepository;
    private final SupportTicketAttachmentRepository supportTicketAttachmentRepository;
    private final SupportTicketHistoryRepository supportTicketHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    // =========================================================
    // CREATE TICKET
    // =========================================================

    @Override
    public SupportTicketDto createTicket(
            CreateSupportTicketRequest request) {

        User currentUser = getCurrentUser();

        /*
         * EMPLOYEE / MANAGER / HR can create tickets.
         *
         * ADMIN and IT_SUPPORT cannot create tickets.
         */
        checkCreateTicketPermission(currentUser);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Ticket request cannot be null"
            );
        }

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Subject is required"
            );
        }

        if (request.getPriority() == null) {

            throw new IllegalArgumentException(
                    "Priority is required"
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Description is required"
            );
        }

        SupportTicket ticket = new SupportTicket();

        ticket.setTicketNumber(
                "TEMP-" + System.currentTimeMillis()
        );

        ticket.setSubject(
                request.getSubject()
        );

        ticket.setCategory(
                request.getCategory()
        );

        /*
         * Employee / Manager / HR selects priority
         * during ticket creation.
         *
         * Priority cannot be changed later.
         */
        ticket.setPriority(
                request.getPriority()
        );

        ticket.setDescription(
                request.getDescription()
        );

        ticket.setCreatedBy(
                currentUser
        );

        /*
         * Every new ticket starts with OPEN.
         */
        ticket.setStatus(
                TicketStatus.OPEN
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        savedTicket.setTicketNumber(
                generateTicketNumber(
                        savedTicket.getId()
                )
        );

        savedTicket =
                supportTicketRepository.save(
                        savedTicket
                );

        addHistory(
                savedTicket,
                currentUser,
                "TICKET_CREATED",
                null,
                TicketStatus.OPEN.name()
        );

        /*
         * Notify all active ADMIN users.
         */
        notifyAdminsNewTicket(
                savedTicket
        );

        return mapToDto(
                savedTicket
        );
    }


    // =========================================================
    // GET TICKET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SupportTicketDto getTicketById(
            Long ticketId) {

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        checkViewPermission(
                ticket,
                currentUser
        );

        return mapToDto(ticket);
    }


    // =========================================================
    // GET MY TICKETS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDto> getMyTickets() {

        User currentUser =
                getCurrentUser();

        return supportTicketRepository
                .findByCreatedByIdOrderByCreatedAtDesc(
                        currentUser.getId()
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    // =========================================================
    // GET ALL TICKETS - ADMIN
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDto> getAllTickets() {

        User currentUser =
                getCurrentUser();

        checkAdmin(currentUser);

        return supportTicketRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    // =========================================================
    // GET ASSIGNED TICKETS - IT SUPPORT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDto> getAssignedTickets() {

        User currentUser =
                getCurrentUser();

        checkRole(
                currentUser,
                RoleType.IT_SUPPORT
        );

        return supportTicketRepository
                .findByAssignedToIdOrderByCreatedAtDesc(
                        currentUser.getId()
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    // =========================================================
    // ASSIGN TICKET - ADMIN ONLY
    // =========================================================

    @Override
    public SupportTicketDto assignTicket(
            Long ticketId,
            Long assignedToId) {

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        /*
         * Only ADMIN can assign.
         */
        checkAdmin(currentUser);

        if (assignedToId == null) {

            throw new IllegalArgumentException(
                    "IT Support user is required"
            );
        }

        User assignedUser =
                userRepository.findById(
                        assignedToId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Assigned user not found"
                        ));

        /*
         * Only IT SUPPORT can be assigned.
         */
        checkRole(
                assignedUser,
                RoleType.IT_SUPPORT
        );

        String oldValue =
                ticket.getAssignedTo() == null
                        ? "UNASSIGNED"
                        : getFullName(
                        ticket.getAssignedTo()
                );

        ticket.setAssignedTo(
                assignedUser
        );

        ticket.setAssignedAt(
                LocalDateTime.now()
        );

        /*
         * OPEN -> ASSIGNED
         */
        if (ticket.getStatus() ==
                TicketStatus.OPEN) {

            ticket.setStatus(
                    TicketStatus.ASSIGNED
            );
        }

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        addHistory(
                savedTicket,
                currentUser,
                "TICKET_ASSIGNED",
                oldValue,
                getFullName(assignedUser)
        );

        notificationService.createNotification(
                assignedUser.getId(),
                "Ticket Assigned",
                "Support ticket "
                        + savedTicket.getTicketNumber()
                        + " has been assigned to you.",
                NotificationType.SYSTEM,
                "SUPPORT_TICKET",
                savedTicket.getId()
        );

        return mapToDto(
                savedTicket
        );
    }


    // =========================================================
    // UPDATE TICKET
    // =========================================================

    @Override
    public SupportTicketDto updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Update request cannot be null"
            );
        }

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        checkManagementPermission(
                ticket,
                currentUser
        );


        // =====================================================
        // PRIORITY
        // =====================================================

        /*
         * Priority is selected during creation.
         *
         * After creation:
         *
         * ADMIN       -> Cannot change
         * IT SUPPORT  -> Cannot change
         * EMPLOYEE    -> Cannot change
         * MANAGER     -> Cannot change
         * HR          -> Cannot change
         */
        if (request.getPriority() != null &&
                request.getPriority() != ticket.getPriority()) {

            throw new RuntimeException(
                    "Ticket priority cannot be changed"
            );
        }


        // =====================================================
        // ASSIGN TICKET
        // =====================================================

        /*
         * Only ADMIN can assign.
         */
        if (request.getAssignedToId() != null) {

            if (!isAdmin(currentUser)) {

                throw new RuntimeException(
                        "Only Admin can assign tickets"
                );
            }

            User assignedUser =
                    userRepository.findById(
                            request.getAssignedToId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Assigned user not found"
                            ));

            /*
             * Only IT SUPPORT users can be assigned.
             */
            checkRole(
                    assignedUser,
                    RoleType.IT_SUPPORT
            );

            String oldAssignee =
                    ticket.getAssignedTo() == null
                            ? "UNASSIGNED"
                            : getFullName(
                            ticket.getAssignedTo()
                    );

            ticket.setAssignedTo(
                    assignedUser
            );

            ticket.setAssignedAt(
                    LocalDateTime.now()
            );

            /*
             * OPEN -> ASSIGNED
             */
            if (ticket.getStatus() ==
                    TicketStatus.OPEN) {

                ticket.setStatus(
                        TicketStatus.ASSIGNED
                );
            }

            addHistory(
                    ticket,
                    currentUser,
                    "TICKET_ASSIGNED",
                    oldAssignee,
                    getFullName(assignedUser)
            );

            notificationService.createNotification(
                    assignedUser.getId(),
                    "Ticket Assigned",
                    "Support ticket "
                            + ticket.getTicketNumber()
                            + " has been assigned to you.",
                    NotificationType.SYSTEM,
                    "SUPPORT_TICKET",
                    ticket.getId()
            );
        }


        // =====================================================
        // STATUS
        // =====================================================

        /*
         * ADMIN cannot change status.
         *
         * IT SUPPORT can change status of assigned tickets.
         *
         * Employee / Manager / HR cannot change status
         * through updateTicket().
         *
         * They can only confirm and close their own
         * RESOLVED ticket using closeTicket().
         */
        if (request.getStatus() != null &&
                request.getStatus() != ticket.getStatus()) {

            TicketStatus oldStatus =
                    ticket.getStatus();

            TicketStatus newStatus =
                    request.getStatus();

            /*
             * Validate status transition.
             */
            validateStatusTransition(
                    oldStatus,
                    newStatus
            );

            /*
             * Validate role permission.
             */
            validateStatusChangePermission(
                    ticket,
                    currentUser,
                    newStatus
            );

            ticket.setStatus(
                    newStatus
            );

            /*
             * RESOLVED
             */
            if (newStatus ==
                    TicketStatus.RESOLVED) {

                ticket.setResolvedAt(
                        LocalDateTime.now()
                );

                notifyTicketResolved(
                        ticket
                );
            }

            /*
             * CLOSED should normally happen only
             * through closeTicket().
             */
            if (newStatus ==
                    TicketStatus.CLOSED) {

                ticket.setClosedAt(
                        LocalDateTime.now()
                );
            }

            /*
             * REOPENED is not part of current workflow.
             */
            if (newStatus ==
                    TicketStatus.REOPENED) {

                ticket.setResolvedAt(null);

                ticket.setClosedAt(null);
            }

            addHistory(
                    ticket,
                    currentUser,
                    "STATUS_CHANGED",
                    oldStatus.name(),
                    newStatus.name()
            );
        }


        // =====================================================
        // SAVE
        // =====================================================

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        return mapToDto(
                savedTicket
        );
    }


    // =========================================================
    // ADD COMMENT
    // =========================================================

    @Override
    public SupportTicketCommentDto addComment(
            Long ticketId,
            String comment) {

        if (comment == null ||
                comment.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Comment cannot be empty"
            );
        }

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        checkCommentPermission(
                ticket,
                currentUser
        );

        SupportTicketComment ticketComment =
                new SupportTicketComment();

        ticketComment.setTicket(
                ticket
        );

        ticketComment.setUser(
                currentUser
        );

        ticketComment.setComment(
                comment.trim()
        );

        SupportTicketComment savedComment =
                supportTicketCommentRepository.save(
                        ticketComment
                );

        addHistory(
                ticket,
                currentUser,
                "COMMENT_ADDED",
                null,
                null
        );

        return mapCommentToDto(
                savedComment
        );
    }


    // =========================================================
    // RESOLVE TICKET - IT SUPPORT ONLY
    // =========================================================

    @Override
    public SupportTicketDto resolveTicket(
            Long ticketId) {

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        /*
         * Only IT SUPPORT can resolve.
         */
        checkRole(
                currentUser,
                RoleType.IT_SUPPORT
        );

        /*
         * IT Support must be assigned to this ticket.
         */
        checkAssignedAgent(
                ticket,
                currentUser
        );

        if (ticket.getStatus() !=
                TicketStatus.IN_PROGRESS &&
                ticket.getStatus() !=
                        TicketStatus.WAITING_FOR_USER) {

            throw new IllegalStateException(
                    "Only IN_PROGRESS or WAITING_FOR_USER tickets can be resolved"
            );
        }

        TicketStatus oldStatus =
                ticket.getStatus();

        ticket.setStatus(
                TicketStatus.RESOLVED
        );

        ticket.setResolvedAt(
                LocalDateTime.now()
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        addHistory(
                savedTicket,
                currentUser,
                "STATUS_CHANGED",
                oldStatus.name(),
                TicketStatus.RESOLVED.name()
        );

        /*
         * Notify ticket creator.
         */
        notifyTicketResolved(
                savedTicket
        );

        return mapToDto(
                savedTicket
        );
    }


    // =========================================================
    // CLOSE TICKET - REQUESTER CONFIRMATION
    // =========================================================

    @Override
    public SupportTicketDto closeTicket(
            Long ticketId) {

        SupportTicket ticket =
                findTicketById(ticketId);

        User currentUser =
                getCurrentUser();

        /*
         * Employee / Manager / HR can confirm
         * their own resolved ticket.
         */
        checkCloseTicketPermission(
                ticket,
                currentUser
        );

        /*
         * Only ticket owner can close.
         */
        checkTicketOwner(
                ticket,
                currentUser
        );

        /*
         * Ticket must be RESOLVED.
         */
        if (ticket.getStatus() !=
                TicketStatus.RESOLVED) {

            throw new IllegalStateException(
                    "Only RESOLVED tickets can be closed"
            );
        }

        TicketStatus oldStatus =
                ticket.getStatus();

        ticket.setStatus(
                TicketStatus.CLOSED
        );

        ticket.setClosedAt(
                LocalDateTime.now()
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(
                        ticket
                );

        addHistory(
                savedTicket,
                currentUser,
                "STATUS_CHANGED",
                oldStatus.name(),
                TicketStatus.CLOSED.name()
        );

        return mapToDto(
                savedTicket
        );
    }


    // =========================================================
    // REOPEN TICKET
    // =========================================================

    @Override
    public SupportTicketDto reopenTicket(
            Long ticketId) {

        /*
         * REOPEN is not part of current workflow.
         */
        throw new IllegalStateException(
                "Reopen ticket is not available in the current workflow"
        );
    }


    // =========================================================
    // STATUS TRANSITION
    // =========================================================

    private void validateStatusTransition(
            TicketStatus currentStatus,
            TicketStatus newStatus) {

        boolean valid =
                switch (currentStatus) {

                    case OPEN ->
                            newStatus == TicketStatus.ASSIGNED;

                    case ASSIGNED ->
                            newStatus == TicketStatus.IN_PROGRESS;

                    case IN_PROGRESS ->
                            newStatus ==
                                    TicketStatus.WAITING_FOR_USER
                                    || newStatus ==
                                    TicketStatus.RESOLVED;

                    case WAITING_FOR_USER ->
                            newStatus ==
                                    TicketStatus.IN_PROGRESS
                                    || newStatus ==
                                    TicketStatus.RESOLVED;

                    case RESOLVED ->
                            newStatus == TicketStatus.CLOSED;

                    case CLOSED ->
                            false;

                    case REOPENED ->
                            newStatus == TicketStatus.IN_PROGRESS;
                };

        if (!valid) {

            throw new IllegalStateException(
                    "Invalid ticket status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }


    // =========================================================
    // STATUS CHANGE PERMISSION
    // =========================================================

    private void validateStatusChangePermission(
            SupportTicket ticket,
            User user,
            TicketStatus newStatus) {

        /*
         * ADMIN
         *
         * Admin can assign tickets only.
         * Admin cannot change status.
         */
        if (isAdmin(user)) {

            throw new RuntimeException(
                    "Admin cannot change ticket status"
            );
        }


        /*
         * IT SUPPORT
         *
         * Only assigned IT Support can change status.
         */
        if (isRole(
                user,
                RoleType.IT_SUPPORT
        )) {

            checkAssignedAgent(
                    ticket,
                    user
            );

            /*
             * IT Support cannot close or reopen.
             */
            if (newStatus ==
                    TicketStatus.CLOSED ||
                    newStatus ==
                            TicketStatus.REOPENED) {

                throw new RuntimeException(
                        "IT Support cannot close or reopen tickets"
                );
            }

            return;
        }


        /*
         * EMPLOYEE / MANAGER / HR
         *
         * They cannot change status through updateTicket().
         *
         * They can only confirm and close their own
         * RESOLVED ticket through closeTicket().
         */
        if (isRequesterRole(user)) {

            throw new RuntimeException(
                    "You can only confirm and close your resolved ticket"
            );
        }


        throw new RuntimeException(
                "You are not authorized to change ticket status"
        );
    }


    // =========================================================
    // VIEW PERMISSION
    // =========================================================

    private void checkViewPermission(
            SupportTicket ticket,
            User user) {

        /*
         * ADMIN can view all.
         */
        if (isAdmin(user)) {
            return;
        }

        /*
         * Ticket creator can view own ticket.
         */
        if (isTicketOwner(
                ticket,
                user
        )) {
            return;
        }

        /*
         * IT Support can view assigned ticket.
         */
        if (isRole(
                user,
                RoleType.IT_SUPPORT
        ) && isAssignedAgent(
                ticket,
                user
        )) {

            return;
        }

        throw new RuntimeException(
                "You are not authorized to view this ticket"
        );
    }


    // =========================================================
    // MANAGEMENT PERMISSION
    // =========================================================

    private void checkManagementPermission(
            SupportTicket ticket,
            User user) {

        /*
         * ADMIN can manage assignment.
         */
        if (isAdmin(user)) {
            return;
        }

        /*
         * IT Support can manage only assigned tickets.
         */
        if (isRole(
                user,
                RoleType.IT_SUPPORT
        )) {

            checkAssignedAgent(
                    ticket,
                    user
            );

            return;
        }

        /*
         * EMPLOYEE / MANAGER / HR cannot manage
         * existing ticket through updateTicket().
         */
        throw new RuntimeException(
                "You are not authorized to manage this ticket"
        );
    }


    // =========================================================
    // COMMENT PERMISSION
    // =========================================================

    private void checkCommentPermission(
            SupportTicket ticket,
            User user) {

        /*
         * ADMIN can comment.
         */
        if (isAdmin(user)) {
            return;
        }

        /*
         * Ticket creator can comment on own ticket.
         */
        if (isTicketOwner(
                ticket,
                user
        )) {

            return;
        }

        /*
         * Assigned IT Support can comment.
         */
        if (isRole(
                user,
                RoleType.IT_SUPPORT
        ) && isAssignedAgent(
                ticket,
                user
        )) {

            return;
        }

        throw new RuntimeException(
                "You are not authorized to comment on this ticket"
        );
    }


    // =========================================================
    // CLOSE TICKET PERMISSION
    // =========================================================

    private void checkCloseTicketPermission(
            SupportTicket ticket,
            User user) {

        /*
         * Only Employee / Manager / HR can confirm
         * and close their own resolved ticket.
         */
        if (!isRequesterRole(user)) {

            throw new RuntimeException(
                    "Only Employee, Manager or HR can confirm and close a ticket"
            );
        }

        /*
         * Only ticket creator can close.
         */
        checkTicketOwner(
                ticket,
                user
        );
    }


    // =========================================================
    // CREATE TICKET PERMISSION
    // =========================================================

    private void checkCreateTicketPermission(
            User user) {

        /*
         * EMPLOYEE / MANAGER / HR can create tickets.
         */
        if (isRequesterRole(user)) {
            return;
        }

        /*
         * ADMIN cannot create tickets.
         */
        if (isAdmin(user)) {

            throw new RuntimeException(
                    "Admin cannot create support tickets"
            );
        }

        /*
         * IT SUPPORT cannot create tickets.
         */
        if (isRole(
                user,
                RoleType.IT_SUPPORT
        )) {

            throw new RuntimeException(
                    "IT Support cannot create support tickets"
            );
        }

        throw new RuntimeException(
                "You are not authorized to create support tickets"
        );
    }


    // =========================================================
    // CHECK TICKET OWNER
    // =========================================================

    private void checkTicketOwner(
            SupportTicket ticket,
            User user) {

        if (!isTicketOwner(
                ticket,
                user
        )) {

            throw new RuntimeException(
                    "You can only manage your own tickets"
            );
        }
    }


    // =========================================================
    // CHECK ASSIGNED AGENT
    // =========================================================

    private void checkAssignedAgent(
            SupportTicket ticket,
            User user) {

        if (!isAssignedAgent(
                ticket,
                user
        )) {

            throw new RuntimeException(
                    "This ticket is not assigned to you"
            );
        }
    }


    // =========================================================
    // IS TICKET OWNER
    // =========================================================

    private boolean isTicketOwner(
            SupportTicket ticket,
            User user) {

        return ticket.getCreatedBy() != null
                && ticket.getCreatedBy().getId() != null
                && ticket.getCreatedBy().getId()
                .equals(user.getId());
    }


    // =========================================================
    // IS ASSIGNED AGENT
    // =========================================================

    private boolean isAssignedAgent(
            SupportTicket ticket,
            User user) {

        return ticket.getAssignedTo() != null
                && ticket.getAssignedTo().getId() != null
                && ticket.getAssignedTo().getId()
                .equals(user.getId());
    }


    // =========================================================
    // IS REQUESTER ROLE
    // =========================================================

    private boolean isRequesterRole(
            User user) {

        return isRole(
                user,
                RoleType.EMPLOYEE
        )
                || isRole(
                user,
                RoleType.MANAGER
        )
                || isRole(
                user,
                RoleType.HR
        );
    }


    // =========================================================
    // CHECK ADMIN
    // =========================================================

    private void checkAdmin(
            User user) {

        if (!isAdmin(user)) {

            throw new RuntimeException(
                    "Only Admin can perform this operation"
            );
        }
    }


    // =========================================================
    // CHECK ROLE
    // =========================================================

    private void checkRole(
            User user,
            RoleType requiredRole) {

        if (!isRole(
                user,
                requiredRole
        )) {

            throw new RuntimeException(
                    "User does not have required role: "
                            + requiredRole
            );
        }
    }


    // =========================================================
    // IS ADMIN
    // =========================================================

    private boolean isAdmin(
            User user) {

        return isRole(
                user,
                RoleType.ADMIN
        );
    }


    // =========================================================
    // IS ROLE
    // =========================================================

    private boolean isRole(
            User user,
            RoleType roleType) {

        return user != null
                && user.getRole() != null
                && user.getRole().getRoleName() ==
                roleType;
    }


    // =========================================================
    // FIND TICKET
    // =========================================================

    private SupportTicket findTicketById(
            Long ticketId) {

        if (ticketId == null) {

            throw new IllegalArgumentException(
                    "Ticket ID is required"
            );
        }

        return supportTicketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Support ticket not found"
                        ));
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        ));
    }


    // =========================================================
    // GENERATE TICKET NUMBER
    // =========================================================

    private String generateTicketNumber(
            Long ticketId) {

        return String.format(
                "IT-%d-%06d",
                LocalDateTime.now().getYear(),
                ticketId
        );
    }


    // =========================================================
    // ADD HISTORY
    // =========================================================

    private void addHistory(
            SupportTicket ticket,
            User user,
            String action,
            String oldValue,
            String newValue) {

        SupportTicketHistory history =
                new SupportTicketHistory();

        history.setTicket(
                ticket
        );

        history.setChangedBy(
                user
        );

        history.setAction(
                action
        );

        history.setOldValue(
                oldValue
        );

        history.setNewValue(
                newValue
        );

        supportTicketHistoryRepository.save(
                history
        );
    }


    // =========================================================
    // NOTIFY ADMINS - NEW TICKET
    // =========================================================

    private void notifyAdminsNewTicket(
            SupportTicket ticket) {

        if (ticket == null ||
                ticket.getId() == null) {

            return;
        }

        List<User> admins =
                userRepository.findByRoleRoleName(
                        RoleType.ADMIN
                );

        for (User admin : admins) {

            if (!admin.isActive()) {
                continue;
            }

            notificationService.createNotification(
                    admin.getId(),
                    "New Support Ticket",
                    getFullName(
                            ticket.getCreatedBy()
                    )
                            + " created support ticket "
                            + ticket.getTicketNumber()
                            + ".",
                    NotificationType.SYSTEM,
                    "SUPPORT_TICKET",
                    ticket.getId()
            );
        }
    }


    // =========================================================
    // NOTIFY REQUESTER - TICKET RESOLVED
    // =========================================================

    private void notifyTicketResolved(
            SupportTicket ticket) {

        if (ticket == null ||
                ticket.getCreatedBy() == null ||
                ticket.getCreatedBy().getId() == null) {

            return;
        }

        String title =
                "Support Ticket Resolved";

        String message =
                "Your support ticket "
                        + ticket.getTicketNumber()
                        + " has been resolved. "
                        + "Please confirm the resolution to close the ticket.";

        notificationService.createNotification(
                ticket.getCreatedBy().getId(),
                title,
                message,
                NotificationType.SYSTEM,
                "SUPPORT_TICKET",
                ticket.getId()
        );
    }


    // =========================================================
    // MAP TICKET DTO
    // =========================================================

    private SupportTicketDto mapToDto(
            SupportTicket ticket) {

        SupportTicketDto dto =
                new SupportTicketDto();

        dto.setId(
                ticket.getId()
        );

        dto.setTicketNumber(
                ticket.getTicketNumber()
        );

        dto.setSubject(
                ticket.getSubject()
        );

        dto.setCategory(
                ticket.getCategory()
        );

        dto.setPriority(
                ticket.getPriority()
        );

        dto.setStatus(
                ticket.getStatus()
        );

        dto.setDescription(
                ticket.getDescription()
        );


        // ---------------------------------------------------------
        // CREATED BY
        // ---------------------------------------------------------

        if (ticket.getCreatedBy() != null) {

            dto.setCreatedById(
                    ticket.getCreatedBy().getId()
            );

            dto.setCreatedByName(
                    getFullName(
                            ticket.getCreatedBy()
                    )
            );
        }


        // ---------------------------------------------------------
        // ASSIGNED TO
        // ---------------------------------------------------------

        if (ticket.getAssignedTo() != null) {

            dto.setAssignedToId(
                    ticket.getAssignedTo().getId()
            );

            dto.setAssignedToName(
                    getFullName(
                            ticket.getAssignedTo()
                    )
            );
        }


        // ---------------------------------------------------------
        // DATES
        // ---------------------------------------------------------

        dto.setCreatedAt(
                ticket.getCreatedAt()
        );

        dto.setUpdatedAt(
                ticket.getUpdatedAt()
        );

        dto.setAssignedAt(
                ticket.getAssignedAt()
        );

        dto.setResolvedAt(
                ticket.getResolvedAt()
        );

        dto.setClosedAt(
                ticket.getClosedAt()
        );


        // ---------------------------------------------------------
        // COMMENTS
        // ---------------------------------------------------------

        List<SupportTicketCommentDto> comments =
                supportTicketCommentRepository
                        .findByTicketIdOrderByCreatedAtAsc(
                                ticket.getId()
                        )
                        .stream()
                        .map(this::mapCommentToDto)
                        .toList();

        dto.setComments(
                comments
        );


        // ---------------------------------------------------------
        // ATTACHMENTS
        // ---------------------------------------------------------

        List<SupportTicketAttachmentDto> attachments =
                supportTicketAttachmentRepository
                        .findByTicketIdOrderByUploadedAtAsc(
                                ticket.getId()
                        )
                        .stream()
                        .map(this::mapAttachmentToDto)
                        .toList();

        dto.setAttachments(
                attachments
        );


        // ---------------------------------------------------------
        // HISTORY
        // ---------------------------------------------------------

        List<SupportTicketHistoryDto> history =
                supportTicketHistoryRepository
                        .findByTicketIdOrderByChangedAtAsc(
                                ticket.getId()
                        )
                        .stream()
                        .map(this::mapHistoryToDto)
                        .toList();

        dto.setHistory(
                history
        );

        return dto;
    }


    // =========================================================
    // MAP COMMENT DTO
    // =========================================================

    private SupportTicketCommentDto mapCommentToDto(
            SupportTicketComment comment) {

        SupportTicketCommentDto dto =
                new SupportTicketCommentDto();

        dto.setId(
                comment.getId()
        );

        if (comment.getUser() != null) {

            dto.setUserId(
                    comment.getUser().getId()
            );

            dto.setUserName(
                    getFullName(
                            comment.getUser()
                    )
            );
        }

        dto.setComment(
                comment.getComment()
        );

        dto.setCreatedAt(
                comment.getCreatedAt()
        );

        return dto;
    }


    // =========================================================
    // MAP ATTACHMENT DTO
    // =========================================================

    private SupportTicketAttachmentDto mapAttachmentToDto(
            SupportTicketAttachment attachment) {

        SupportTicketAttachmentDto dto =
                new SupportTicketAttachmentDto();

        dto.setId(
                attachment.getId()
        );

        dto.setFileName(
                attachment.getFileName()
        );

        dto.setFileType(
                attachment.getFileType()
        );

        dto.setUploadedAt(
                attachment.getUploadedAt()
        );

        if (attachment.getUploadedBy() != null) {

            dto.setUploadedByName(
                    getFullName(
                            attachment.getUploadedBy()
                    )
            );
        }

        return dto;
    }


    // =========================================================
    // MAP HISTORY DTO
    // =========================================================

    private SupportTicketHistoryDto mapHistoryToDto(
            SupportTicketHistory history) {

        SupportTicketHistoryDto dto =
                new SupportTicketHistoryDto();

        dto.setId(
                history.getId()
        );

        if (history.getChangedBy() != null) {

            dto.setChangedByName(
                    getFullName(
                            history.getChangedBy()
                    )
            );
        }

        dto.setAction(
                history.getAction()
        );

        dto.setOldValue(
                history.getOldValue()
        );

        dto.setNewValue(
                history.getNewValue()
        );

        dto.setChangedAt(
                history.getChangedAt()
        );

        return dto;
    }


    // =========================================================
    // GET FULL NAME
    // =========================================================

    private String getFullName(
            User user) {

        if (user == null) {
            return "";
        }

        return user.getFirstName()
                + " "
                + user.getLastName();
    }
}