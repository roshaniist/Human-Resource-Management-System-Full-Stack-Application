package com.example.emps.service.impl;

import com.example.emps.dto.PerformanceReviewDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.PerformanceReview;
import com.example.emps.entity.User;
import com.example.emps.enums.NotificationType;
import com.example.emps.enums.PerformanceReviewStatus;
import com.example.emps.exception.BadRequestException;
import com.example.emps.exception.ResourceNotFoundException;
import com.example.emps.exception.UnauthorizedException;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.PerformanceReviewRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.NotificationService;
import com.example.emps.service.PerformanceReviewService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PerformanceReviewServiceImpl
        implements PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PerformanceReviewServiceImpl(
            PerformanceReviewRepository performanceReviewRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    @Override
    public PerformanceReviewDto createReview(
            PerformanceReviewDto reviewDto) {

        if (reviewDto == null) {
            throw new BadRequestException(
                    "Performance review data is required"
            );
        }

        if (reviewDto.getEmployeeId() == null) {
            throw new BadRequestException(
                    "Employee ID is required"
            );
        }

        Employee employee = employeeRepository.findById(
                reviewDto.getEmployeeId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Employee not found"
                )
        );

        User loggedInUser = getLoggedInUser();

        /*
         * Manager can create reviews only for employees
         * belonging to the manager's department.
         */
        if (isManager()) {
            checkManagerEmployeeAccess(employee);
        }

        /*
         * Prevent duplicate review for the same employee
         * and review period.
         */
        if (reviewDto.getReviewPeriod() != null &&
                performanceReviewRepository
                        .existsByEmployeeIdAndReviewPeriod(
                                employee.getId(),
                                reviewDto.getReviewPeriod()
                        )) {

            throw new BadRequestException(
                    "A performance review already exists for this employee " +
                            "and review period"
            );
        }

        /*
         * For Manager, the logged-in Manager becomes the reviewer.
         *
         * For ADMIN/HR, reviewerId is still accepted from the request
         * to preserve the current frontend/API behaviour.
         */
        User reviewer;

        if (isManager()) {

            reviewer = loggedInUser;

        } else {

            if (reviewDto.getReviewerId() == null) {
                throw new BadRequestException(
                        "Reviewer ID is required"
                );
            }

            reviewer = userRepository.findById(
                    reviewDto.getReviewerId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Reviewer not found"
                    )
            );
        }

        PerformanceReview review =
                new PerformanceReview();

        review.setReviewDate(
                reviewDto.getReviewDate()
        );

        review.setRating(
                reviewDto.getRating()
        );

        review.setReviewPeriod(
                reviewDto.getReviewPeriod()
        );

        review.setComments(
                reviewDto.getComments()
        );

        review.setEmployee(employee);

        review.setReviewer(reviewer);

        /*
         * Status is controlled by backend.
         * Client cannot create a review directly as
         * SUBMITTED/COMPLETED/etc.
         */
        review.setStatus(
                PerformanceReviewStatus.DRAFT
        );

        review.setEmployeeAcknowledged(false);
        review.setAcknowledgedAt(null);

        PerformanceReview savedReview =
                performanceReviewRepository.save(review);

        notifyEmployee(
                savedReview,
                "Performance Review Created",
                "A new performance review has been created for your review period "
                        + getReviewPeriod(savedReview)
                        + "."
        );

        return convertToDto(savedReview);
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getAllReviews() {

        List<PerformanceReview> reviews =
                performanceReviewRepository.findAll();

        /*
         * ADMIN and HR can see all reviews.
         * MANAGER can see reviews only for employees
         * in the manager's department.
         */
        if (isManager()) {

            Employee manager = getLoggedInEmployee();

            reviews = reviews.stream()
                    .filter(review ->
                            isSameDepartment(
                                    manager,
                                    review.getEmployee()
                            )
                    )
                    .toList();
        }

        return reviews.stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET MY REVIEWS
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getMyReviews() {

        Employee loggedInEmployee =
                getLoggedInEmployee();

        return performanceReviewRepository
                .findByEmployeeIdOrderByReviewDateDesc(
                        loggedInEmployee.getId()
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET REVIEW BY ID
    // =========================================================

    @Override
    public PerformanceReviewDto getReviewById(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        if (isEmployee()) {

            checkEmployeeAccess(
                    review.getEmployee()
            );

        } else if (isManager()) {

            checkManagerEmployeeAccess(
                    review.getEmployee()
            );
        }

        return convertToDto(review);
    }

    // =========================================================
    // GET REVIEWS BY EMPLOYEE
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getReviewsByEmployee(
            Long employeeId) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found"
                                )
                        );

        if (isEmployee()) {

            checkEmployeeAccess(employee);

        } else if (isManager()) {

            checkManagerEmployeeAccess(employee);
        }

        return performanceReviewRepository
                .findByEmployeeIdOrderByReviewDateDesc(
                        employeeId
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET REVIEWS BY EMPLOYEE + STATUS
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getReviewsByEmployeeAndStatus(
            Long employeeId,
            PerformanceReviewStatus status) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found"
                                )
                        );

        if (isEmployee()) {

            checkEmployeeAccess(employee);

        } else if (isManager()) {

            checkManagerEmployeeAccess(employee);
        }

        if (status == null) {
            throw new BadRequestException(
                    "Performance review status is required"
            );
        }

        return performanceReviewRepository
                .findByEmployeeIdAndStatusOrderByReviewDateDesc(
                        employeeId,
                        status
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET REVIEWS BY REVIEWER
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getReviewsByReviewer(
            Long reviewerId) {

        User reviewer =
                userRepository.findById(reviewerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reviewer not found"
                                )
                        );

        /*
         * Manager can only access reviews assigned to
         * himself as reviewer.
         */
        if (isManager()) {

            User loggedInUser =
                    getLoggedInUser();

            if (!loggedInUser.getId()
                    .equals(reviewer.getId())) {

                throw new UnauthorizedException(
                        "Manager can access only their own assigned reviews"
                );
            }
        }

        return performanceReviewRepository
                .findByReviewerIdOrderByReviewDateDesc(
                        reviewerId
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET REVIEWS BY REVIEWER + STATUS
    // =========================================================

    @Override
    public List<PerformanceReviewDto> getReviewsByReviewerAndStatus(
            Long reviewerId,
            PerformanceReviewStatus status) {

        User reviewer =
                userRepository.findById(reviewerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reviewer not found"
                                )
                        );

        if (status == null) {
            throw new BadRequestException(
                    "Performance review status is required"
            );
        }

        /*
         * Manager can only access their own assigned reviews.
         */
        if (isManager()) {

            User loggedInUser =
                    getLoggedInUser();

            if (!loggedInUser.getId()
                    .equals(reviewer.getId())) {

                throw new UnauthorizedException(
                        "Manager can access only their own assigned reviews"
                );
            }
        }

        return performanceReviewRepository
                .findByReviewerIdAndStatusOrderByReviewDateDesc(
                        reviewerId,
                        status
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // UPDATE REVIEW
    // =========================================================

    @Override
    public PerformanceReviewDto updateReview(
            Long id,
            PerformanceReviewDto reviewDto) {

        if (reviewDto == null) {
            throw new BadRequestException(
                    "Performance review data is required"
            );
        }

        PerformanceReview existingReview =
                getReviewEntity(id);

        /*
         * Only DRAFT reviews can be edited.
         */
        if (existingReview.getStatus()
                != PerformanceReviewStatus.DRAFT) {

            throw new BadRequestException(
                    "Only DRAFT performance reviews can be updated"
            );
        }

        if (reviewDto.getEmployeeId() == null) {
            throw new BadRequestException(
                    "Employee ID is required"
            );
        }

        Employee employee =
                employeeRepository.findById(
                        reviewDto.getEmployeeId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found"
                        )
                );

        if (isManager()) {

            checkManagerEmployeeAccess(employee);
        }

        /*
         * If employee/review period is changed,
         * check whether another review already exists.
         */
        boolean employeeChanged =
                existingReview.getEmployee() == null ||
                        !existingReview.getEmployee()
                                .getId()
                                .equals(employee.getId());

        boolean periodChanged =
                existingReview.getReviewPeriod() == null ||
                        !existingReview.getReviewPeriod()
                                .equals(reviewDto.getReviewPeriod());

        if ((employeeChanged || periodChanged)
                && reviewDto.getReviewPeriod() != null
                && performanceReviewRepository
                .existsByEmployeeIdAndReviewPeriod(
                        employee.getId(),
                        reviewDto.getReviewPeriod()
                )) {

            throw new BadRequestException(
                    "A performance review already exists for this employee " +
                            "and review period"
            );
        }

        User reviewer;

        /*
         * Manager remains the reviewer.
         * Client cannot change the reviewer of a Manager-created review.
         */
        if (isManager()) {

            reviewer = getLoggedInUser();

        } else {

            if (reviewDto.getReviewerId() == null) {
                throw new BadRequestException(
                        "Reviewer ID is required"
                );
            }

            reviewer = userRepository.findById(
                    reviewDto.getReviewerId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Reviewer not found"
                    )
            );
        }

        existingReview.setReviewDate(
                reviewDto.getReviewDate()
        );

        existingReview.setRating(
                reviewDto.getRating()
        );

        existingReview.setReviewPeriod(
                reviewDto.getReviewPeriod()
        );

        existingReview.setComments(
                reviewDto.getComments()
        );

        existingReview.setEmployee(employee);

        existingReview.setReviewer(reviewer);

        /*
         * Do not take status from frontend.
         */
        existingReview.setStatus(
                PerformanceReviewStatus.DRAFT
        );

        PerformanceReview updatedReview =
                performanceReviewRepository.save(
                        existingReview
                );

        notifyEmployee(
                updatedReview,
                "Performance Review Updated",
                "Your performance review for "
                        + getReviewPeriod(updatedReview)
                        + " has been updated."
        );

        return convertToDto(updatedReview);
    }

    // =========================================================
    // SUBMIT REVIEW
    // =========================================================

    @Override
    public PerformanceReviewDto submitReview(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        if (isManager()) {
            checkManagerEmployeeAccess(
                    review.getEmployee()
            );
        }

        if (review.getStatus()
                != PerformanceReviewStatus.DRAFT) {

            throw new BadRequestException(
                    "Only DRAFT reviews can be submitted"
            );
        }

        review.setStatus(
                PerformanceReviewStatus.SUBMITTED
        );

        PerformanceReview savedReview =
                performanceReviewRepository.save(review);

        notifyEmployee(
                savedReview,
                "Performance Review Submitted",
                "Your performance review for "
                        + getReviewPeriod(savedReview)
                        + " has been submitted and is ready for your acknowledgement."
        );

        return convertToDto(savedReview);
    }

    // =========================================================
    // ACKNOWLEDGE REVIEW
    // =========================================================

    @Override
    public void acknowledgeReview(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        if (review.getStatus()
                != PerformanceReviewStatus.SUBMITTED) {

            throw new BadRequestException(
                    "Only SUBMITTED reviews can be acknowledged"
            );
        }

        Employee loggedInEmployee =
                getLoggedInEmployee();

        if (review.getEmployee() == null ||
                !review.getEmployee()
                        .getId()
                        .equals(loggedInEmployee.getId())) {

            throw new UnauthorizedException(
                    "You can acknowledge only your own performance review"
            );
        }

        /*
         * Prevent duplicate acknowledgement.
         */
        if (review.isEmployeeAcknowledged()) {

            throw new BadRequestException(
                    "Performance review is already acknowledged"
            );
        }

        review.setEmployeeAcknowledged(true);

        review.setAcknowledgedAt(
                LocalDateTime.now()
        );

        review.setStatus(
                PerformanceReviewStatus.ACKNOWLEDGED
        );

        PerformanceReview savedReview =
                performanceReviewRepository.save(review);

        notifyReviewer(
                savedReview,
                "Performance Review Acknowledged",
                "The employee has acknowledged the performance review for "
                        + getReviewPeriod(savedReview)
                        + "."
        );
    }

    // =========================================================
    // COMPLETE REVIEW
    // =========================================================

    @Override
    public PerformanceReviewDto completeReview(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        if (isManager()) {

            checkManagerEmployeeAccess(
                    review.getEmployee()
            );
        }

        if (review.getStatus()
                != PerformanceReviewStatus.ACKNOWLEDGED) {

            throw new BadRequestException(
                    "Only ACKNOWLEDGED reviews can be completed"
            );
        }

        review.setStatus(
                PerformanceReviewStatus.COMPLETED
        );

        PerformanceReview savedReview =
                performanceReviewRepository.save(review);

        notifyEmployee(
                savedReview,
                "Performance Review Completed",
                "Your performance review for "
                        + getReviewPeriod(savedReview)
                        + " has been completed."
        );

        return convertToDto(savedReview);
    }

    // =========================================================
    // CANCEL REVIEW
    // =========================================================

    @Override
    public PerformanceReviewDto cancelReview(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        if (isManager()) {

            checkManagerEmployeeAccess(
                    review.getEmployee()
            );
        }

        if (review.getStatus()
                == PerformanceReviewStatus.COMPLETED) {

            throw new BadRequestException(
                    "Completed performance review cannot be cancelled"
            );
        }

        if (review.getStatus()
                == PerformanceReviewStatus.CANCELLED) {

            throw new BadRequestException(
                    "Performance review is already cancelled"
            );
        }

        review.setStatus(
                PerformanceReviewStatus.CANCELLED
        );

        PerformanceReview savedReview =
                performanceReviewRepository.save(review);

        notifyEmployee(
                savedReview,
                "Performance Review Cancelled",
                "Your performance review for "
                        + getReviewPeriod(savedReview)
                        + " has been cancelled."
        );

        return convertToDto(savedReview);
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    @Override
    public void deleteReview(
            Long id) {

        PerformanceReview review =
                getReviewEntity(id);

        /*
         * Only DRAFT or CANCELLED reviews should be deleted.
         * Submitted/Acknowledged/Completed reviews are part
         * of the audit workflow and should be preserved.
         */
        if (review.getStatus() != PerformanceReviewStatus.DRAFT &&
                review.getStatus() != PerformanceReviewStatus.CANCELLED) {

            throw new BadRequestException(
                    "Only DRAFT or CANCELLED performance reviews can be deleted"
            );
        }

        Employee employee =
                review.getEmployee();

        User reviewer =
                review.getReviewer();

        Long reviewId =
                review.getId();

        String reviewPeriod =
                getReviewPeriod(review);

        performanceReviewRepository.delete(review);

        notifyEmployee(
                employee,
                reviewId,
                "Performance Review Deleted",
                "Your performance review for "
                        + reviewPeriod
                        + " has been deleted."
        );

        notifyReviewer(
                reviewer,
                reviewId,
                "Performance Review Deleted",
                "A performance review for "
                        + reviewPeriod
                        + " has been deleted."
        );
    }

    // =========================================================
    // NOTIFY EMPLOYEE
    // =========================================================

    private void notifyEmployee(
            PerformanceReview review,
            String title,
            String message) {

        if (review == null) {
            return;
        }

        notifyEmployee(
                review.getEmployee(),
                review.getId(),
                title,
                message
        );
    }

    private void notifyEmployee(
            Employee employee,
            Long reviewId,
            String title,
            String message) {

        if (employee == null ||
                employee.getUser() == null ||
                employee.getUser().getId() == null) {

            return;
        }

        notificationService.createNotification(
                employee.getUser().getId(),
                title,
                message,
                NotificationType.PERFORMANCE,
                "PERFORMANCE_REVIEW",
                reviewId
        );
    }

    // =========================================================
    // NOTIFY REVIEWER
    // =========================================================

    private void notifyReviewer(
            PerformanceReview review,
            String title,
            String message) {

        if (review == null) {
            return;
        }

        notifyReviewer(
                review.getReviewer(),
                review.getId(),
                title,
                message
        );
    }

    private void notifyReviewer(
            User reviewer,
            Long reviewId,
            String title,
            String message) {

        if (reviewer == null ||
                reviewer.getId() == null) {

            return;
        }

        notificationService.createNotification(
                reviewer.getId(),
                title,
                message,
                NotificationType.PERFORMANCE,
                "PERFORMANCE_REVIEW",
                reviewId
        );
    }

    // =========================================================
    // REVIEW PERIOD
    // =========================================================

    private String getReviewPeriod(
            PerformanceReview review) {

        if (review == null ||
                review.getReviewPeriod() == null ||
                review.getReviewPeriod().isBlank()) {

            return "the selected period";
        }

        return review.getReviewPeriod();
    }

    // =========================================================
    // GET REVIEW ENTITY
    // =========================================================

    private PerformanceReview getReviewEntity(
            Long id) {

        if (id == null) {

            throw new BadRequestException(
                    "Performance review ID is required"
            );
        }

        return performanceReviewRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Performance review not found"
                        )
                );
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new UnauthorizedException(
                    "Authentication required"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );
    }

    // =========================================================
    // CURRENT EMPLOYEE
    // =========================================================

    private Employee getLoggedInEmployee() {

        User loggedInUser =
                getLoggedInUser();

        return employeeRepository
                .findByUserId(loggedInUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee profile not found for logged-in user"
                        )
                );
    }

    // =========================================================
    // EMPLOYEE ROLE
    // =========================================================

    private boolean isEmployee() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_EMPLOYEE".equals(
                                authority.getAuthority()
                        )
                );
    }

    // =========================================================
    // MANAGER ROLE
    // =========================================================

    private boolean isManager() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_MANAGER".equals(
                                authority.getAuthority()
                        )
                );
    }

    // =========================================================
    // EMPLOYEE ACCESS
    // =========================================================

    private void checkEmployeeAccess(
            Employee employee) {

        Employee loggedInEmployee =
                getLoggedInEmployee();

        if (employee == null ||
                employee.getId() == null ||
                !employee.getId()
                        .equals(loggedInEmployee.getId())) {

            throw new UnauthorizedException(
                    "You can access only your own performance review"
            );
        }
    }

    // =========================================================
    // MANAGER EMPLOYEE ACCESS
    // =========================================================

    private void checkManagerEmployeeAccess(
            Employee employee) {

        if (employee == null) {

            throw new ResourceNotFoundException(
                    "Employee not found"
            );
        }

        Employee manager =
                getLoggedInEmployee();

        if (manager.getDepartment() == null ||
                manager.getDepartment().getId() == null) {

            throw new UnauthorizedException(
                    "Manager is not assigned to a department"
            );
        }

        if (employee.getDepartment() == null ||
                employee.getDepartment().getId() == null) {

            throw new UnauthorizedException(
                    "Employee is not assigned to a department"
            );
        }

        if (!manager.getDepartment()
                .getId()
                .equals(
                        employee.getDepartment().getId()
                )) {

            throw new UnauthorizedException(
                    "Manager can access employees only from their department"
            );
        }
    }

    // =========================================================
    // SAME DEPARTMENT CHECK
    // =========================================================

    private boolean isSameDepartment(
            Employee firstEmployee,
            Employee secondEmployee) {

        if (firstEmployee == null ||
                secondEmployee == null ||
                firstEmployee.getDepartment() == null ||
                secondEmployee.getDepartment() == null ||
                firstEmployee.getDepartment().getId() == null ||
                secondEmployee.getDepartment().getId() == null) {

            return false;
        }

        return firstEmployee.getDepartment()
                .getId()
                .equals(
                        secondEmployee.getDepartment().getId()
                );
    }

    // =========================================================
    // DTO CONVERSION
    // =========================================================

    private PerformanceReviewDto convertToDto(
            PerformanceReview review) {

        PerformanceReviewDto dto =
                new PerformanceReviewDto();

        dto.setId(
                review.getId()
        );

        dto.setReviewDate(
                review.getReviewDate()
        );

        dto.setRating(
                review.getRating()
        );

        dto.setReviewPeriod(
                review.getReviewPeriod()
        );

        dto.setComments(
                review.getComments()
        );

        dto.setStatus(
                review.getStatus()
        );

        dto.setEmployeeAcknowledged(
                review.isEmployeeAcknowledged()
        );

        dto.setAcknowledgedAt(
                review.getAcknowledgedAt()
        );

        dto.setCreatedAt(
                review.getCreatedAt()
        );

        dto.setUpdatedAt(
                review.getUpdatedAt()
        );

        if (review.getEmployee() != null) {

            dto.setEmployeeId(
                    review.getEmployee().getId()
            );
        }

        if (review.getReviewer() != null) {

            dto.setReviewerId(
                    review.getReviewer().getId()
            );
        }

        return dto;
    }
}