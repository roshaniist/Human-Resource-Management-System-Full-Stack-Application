# Enterprise Project Management System (EPMS)

**A Full-Stack Enterprise Project Management System built using Java 21, Spring Boot, Angular, Spring Security, JWT Authentication, PostgreSQL, and Docker.**

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-brightgreen)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-Frontend-red)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Containerization-blue)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/Security-JWT-purple)](https://jwt.io/)

EPMS is a centralized enterprise application designed to manage employees, departments, projects, tasks, attendance, leave requests, payroll, performance reviews, documents, reports, IT support activities, and notifications.

The application provides secure authentication, role-based access control, RESTful APIs, and a user-friendly Angular interface to simplify organizational operations and improve workforce management.

---

## Table of Contents

1. Project Overview
2. Key Features
3. Application Modules
4. Technology Stack
5. System Architecture
6. Role-Based Access Control
7. Module Details
8. Security Architecture
9. Database Management
10. Repository Structure
11. Application Setup and Installation
12. Docker Deployment
13. Docker Ports & Application URLs
14. Author
15. License

---

## 1. Project Overview

Enterprise Project Management System (EPMS) is a full-stack web application developed to centralize and streamline organizational operations.

It provides different modules for managing employees, departments, projects, tasks, attendance, leave, payroll, performance, documents, reports, and IT support.

The application follows a three-tier architecture:

- **Frontend:** Angular and TypeScript.
- **Backend:** Java 21 and Spring Boot.
- **Database:** PostgreSQL.
- **Security:** Spring Security and JWT.
- **Containerization:** Docker and Docker Compose.

The Angular frontend communicates with the Spring Boot backend through RESTful APIs. The backend handles business logic, authentication, authorization, and database operations.

### Project Objectives

- Centralize employee and organizational information.
- Simplify project and task management.
- Improve employee attendance tracking.
- Manage employee leave and approval workflows.
- Maintain payroll and salary information.
- Track employee performance.
- Securely manage employee documents.
- Generate employee and department reports.
- Manage IT support requests.
- Provide centralized notifications.
- Implement role-based access control.

---

## 2. Key Features

- Full-stack enterprise application.
- Secure JWT-based authentication.
- Spring Security integration.
- Role-Based Access Control (RBAC).
- RESTful API architecture.
- Employee and department management.
- Project and task management.
- Attendance tracking and working hours calculation.
- Leave approval and rejection workflow.
- Payroll generation and management.
- Employee performance reviews.
- Document and profile photo management.
- Employee and department reporting.
- IT support ticket management.
- Notification management.
- Docker-based application deployment.
- PostgreSQL database integration.

---

## 3. Application Modules

The application consists of 13 major modules.

| No. | Module | Description |
|---|---|---|
| 1 | Authentication & Authorization | Registration, login, password management and JWT authentication |
| 2 | Employee Management | Employee profiles and information |
| 3 | Department Management | Department creation and management |
| 4 | Project Management | Project creation and employee assignments |
| 5 | Task Management | Task creation, assignment and tracking |
| 6 | Attendance Management | Check-in, check-out, breaks and working hours |
| 7 | Leave Management | Leave applications and approval workflow |
| 8 | Payroll Management | Salary, allowances, deductions and payroll |
| 9 | Performance Management | Employee performance reviews |
| 10 | Document Management | Employee document and photo management |
| 11 | Reports | Employee and department reports |
| 12 | IT Support | Support ticket creation and management |
| 13 | Notification Management | Automatic notifications and read/unread tracking |

---

## 4. Technology Stack

| Layer | Technologies |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT |
| API Architecture | REST APIs |
| ORM | Hibernate |
| Data Access | Spring Data JPA |
| Frontend Framework | Angular |
| Frontend Language | TypeScript |
| UI Technologies | HTML5, CSS3, Bootstrap |
| Database | PostgreSQL |
| Build Tool | Maven |
| Containerization | Docker |
| Container Management | Docker Compose |
| Version Control | Git, GitHub |
| Backend IDE | IntelliJ IDEA |
| Frontend IDE | Visual Studio Code |

---

## 5. System Architecture

The application follows a three-tier architecture.

### 5.1 Frontend Architecture

**Angular + TypeScript**

The frontend is responsible for:

- Rendering application pages and dashboards.
- Managing user interactions.
- Communicating with backend REST APIs.
- Handling authentication and route navigation.
- Implementing authentication and role guards.
- Managing HTTP requests using Angular services.
- Attaching JWT tokens using HTTP interceptors.
- Displaying role-based navigation menus.

### 5.2 Backend Architecture

**Java 21 + Spring Boot**

The backend is responsible for:

- Exposing RESTful APIs.
- Processing frontend requests.
- Implementing business logic.
- Managing authentication and authorization.
- Validating incoming requests.
- Performing database operations.
- Managing application modules.
- Handling exceptions and application errors.

The backend follows a layered architecture consisting of:

- Controller Layer
- Service Layer
- Service Implementation Layer
- Repository Layer
- Entity Layer
- DTO Layer
- Security Layer
- Configuration Layer

### 5.3 Database Architecture

**PostgreSQL**

The database stores and manages:

- User and role information.
- Employee records.
- Department information.
- Project and task records.
- Attendance records.
- Leave requests.
- Payroll information.
- Performance reviews.
- Employee documents.
- Support tickets.
- Notifications.

### Architecture Diagram

```text
                 USER
                   |
                   |
          ANGULAR FRONTEND
             (Port 4200)
                   |
                   |
            Angular Services
                   |
             HTTP Interceptor
                   |
               JWT Token
                   |
                   |
            REST API Requests
                   |
                   |
           SPRING BOOT BACKEND
               (Port 8080)
                   |
          SPRING SECURITY + JWT
                   |
                   |
               CONTROLLER
                   |
                SERVICE
                   |
              REPOSITORY
                   |
               HIBERNATE
                   |
                   |
               POSTGRESQL
                (Port 5432)
```

---

## 6. Role-Based Access Control (RBAC)

The application implements Role-Based Access Control using Spring Security and JWT Authentication.

Each user receives permissions according to their assigned role.

### Available Roles

1. ADMIN
2. HR
3. MANAGER
4. EMPLOYEE
5. IT_SUPPORT

### 6.1 ADMIN

- Manages the overall application.
- Manages users and roles.
- Manages employees and departments.
- Manages projects and organizational activities.
- Accesses administrative functionalities.

### 6.2 HR

- Manages employee information.
- Manages department information.
- Handles employee leave requests.
- Manages payroll operations.
- Handles employee performance reviews.
- Accesses HR-related reports.

### 6.3 MANAGER

- Manages assigned projects.
- Assigns tasks to employees.
- Tracks project and task progress.
- Views team information.
- Approves or rejects leave requests according to department authorization rules.
- Monitors employee performance.

### 6.4 EMPLOYEE

- Views personal profile information.
- Views assigned projects and tasks.
- Manages attendance check-in and check-out.
- Applies for leave.
- Views leave history and status.
- Views personal payroll information.
- Uploads permitted personal documents.
- Creates IT support tickets.
- Receives application notifications.

### 6.5 IT_SUPPORT

- Accesses the IT Support module.
- Views assigned support tickets.
- Manages IT support requests.
- Updates ticket status.
- Handles technical issues reported by employees.
- Tracks support ticket progress.
- Manages support-related activities according to assigned permissions.

### Role-Based Access Summary

| Module | ADMIN | HR | MANAGER | EMPLOYEE | IT_SUPPORT |
|---|---|---|---|---|---|
| Authentication | Yes | Yes | Yes | Yes | Yes |
| Employee Management | Yes | Yes | Restricted | Own Profile | Restricted |
| Department Management | Yes | Yes | View | No | No |
| Project Management | Yes | Restricted | Yes | Assigned Projects | No |
| Task Management | Yes | Restricted | Yes | Assigned Tasks | No |
| Attendance | Yes | Yes | Team View | Own Attendance | No |
| Leave Management | Yes | Yes | Approve/Reject | Apply/View Own | No |
| Payroll | Yes | Yes | Restricted | Own Payroll | No |
| Performance | Yes | Yes | Team Reviews | Own Reviews | No |
| Documents | Yes | Yes | Permitted Access | Own Documents | No |
| Reports | Yes | Yes | Permitted Reports | No | No |
| IT Support | Yes | No | No | Create/View Own Tickets | Manage Assigned Tickets |
| Notifications | Yes | Yes | Yes | Yes | Yes |

*Note: The table represents the intended access model. Actual permissions are enforced by backend authorization rules.*

---

## 7. Module Details

### 7.1 Authentication & Authorization

Provides secure user authentication and access management.

**Features:**

- User Registration.
- User Login.
- Forgot Password.
- OTP Verification.
- Reset Password.
- Change Password.
- JWT Token Generation.
- JWT Token Validation.
- Spring Security Integration.
- Role-Based Access Control.

### 7.2 Employee Management

Manages employee information and profiles.

**Features:**

- Add New Employees.
- View Employee Details.
- Update Employee Information.
- Manage Employee Status.
- Employee Profile Management.
- Employee Code Management.
- Employee Information Tracking.

### 7.3 Department Management

Manages organizational departments.

**Features:**

- Create Departments.
- View Department Details.
- Update Department Information.
- Assign Employees to Departments.
- View Department Employees.
- Manage Department Records.

### 7.4 Project Management

Manages organizational projects and employee assignments.

**Features:**

- Create Projects.
- Update Project Information.
- Assign Project Managers.
- Assign Employees to Projects.
- Track Project Status.
- View Project Details.
- Manage Project Activities.

### 7.5 Task Management

Manages tasks associated with projects.

**Features:**

- Create Tasks.
- Assign Tasks to Employees.
- Update Task Information.
- Track Task Status.
- Monitor Task Progress.
- View Assigned Tasks.
- Manage Task Deadlines.

### 7.6 Attendance Management

Tracks employee attendance and working hours.

**Features:**

- Employee Check-In.
- Employee Check-Out.
- Break Start and End.
- Attendance History.
- Working Hours Calculation.
- Attendance Status Tracking.
- Attendance Record Management.

### 7.7 Leave Management

Manages employee leave applications and approval workflows.

**Features:**

- Apply for Leave.
- View Leave History.
- Track Leave Status.
- Approve Leave Requests.
- Reject Leave Requests.
- Rejection Reason Management.
- Leave Type Management.

**Business Rules:**

- Employees can apply for leave and view their own leave records.
- Department Managers and Admins can approve or reject leave requests according to authorization rules.
- A rejection reason is required when rejecting a leave request.
- HR can access leave information according to assigned permissions.

### 7.8 Payroll Management

Manages employee salary and payroll information.

**Features:**

- Generate Payroll.
- Manage Basic Salary.
- Calculate Allowances.
- Manage Bonuses.
- Calculate Deductions.
- Calculate Gross Salary.
- Calculate Net Salary.
- Track Payroll Status.
- Maintain Payroll Payment Date.

**Business Rules:**

- Payroll records are maintained for individual employees and payroll periods.
- Duplicate payroll records for the same employee and period are restricted.
- Paid payroll records cannot be modified or deleted through normal payroll operations.

### 7.9 Performance Management

Manages employee performance evaluation.

**Features:**

- Employee Performance Reviews.
- Performance Tracking.
- Review Information Management.
- Employee Evaluation Records.
- Performance History.

### 7.10 Document Management

Manages employee documents and profile photos.

**Features:**

- Upload Employee Documents.
- Upload Profile Photos.
- View Uploaded Documents.
- Download Documents.
- Manage Employee Documents.
- Access Document Information.

### 7.11 Reports

Provides employee and department reporting functionality.

**Features:**

- Employee Reports.
- Department Reports.
- Employee Summary.
- Employee Statistics.
- Department Information.
- Report Filtering.
- PDF, Excel and CSV Export where supported.

**Employee Report Filters:**

- Employee Search.
- Department.
- Designation.
- Date Range.
- Active/Inactive Status.

### 7.12 IT Support

Provides a centralized platform for handling employee technical support requests.

**Features:**

- Create Support Tickets.
- View Support Tickets.
- Assign Support Tickets.
- Update Ticket Information.
- Track Ticket Status.
- Manage Assigned Tickets.
- Handle Technical Issues.
- Monitor IT Support Activities.

The IT Support module provides a centralized platform for employees to report technical issues and for IT support personnel to manage and resolve those requests.

### 7.13 Notification Management

Provides notifications for different application activities.

**Features:**

- Automatic Notifications.
- Read Notifications.
- Unread Notifications.
- Mark Notifications as Read.
- Notification History.
- Notification Status Management.

---

## 8. Security Architecture

The application uses Spring Security and JWT to secure backend APIs.

### JWT Authentication Flow

1. The user enters login credentials.
2. Angular sends the login request to the backend.
3. Spring Security validates the credentials.
4. The backend generates a JWT token after successful authentication.
5. Angular attaches the token to subsequent authenticated requests.
6. The JWT authentication filter validates incoming requests.
7. The backend checks the user's authorization before allowing access to protected resources.

### Backend Security Components

| Component | Responsibility |
|---|---|
| SecurityConfig | Configures Spring Security |
| CustomUserDetailsService | Loads user details |
| JwtService | Generates and validates JWT tokens |
| JwtAuthenticationFilter | Validates JWT on incoming requests |
| JwtAuthenticationEntryPoint | Handles unauthorized access |
| CorsConfig | Configures cross-origin requests |

### Frontend Security Components

| Component | Responsibility |
|---|---|
| auth.guard.ts | Protects authenticated routes |
| role.guard.ts | Controls role-based route access |
| auth.interceptor.ts | Adds JWT authentication tokens to HTTP requests |

---

## 9. Database Management

**Database: PostgreSQL**

The application uses PostgreSQL for persistent data storage.

Hibernate and Spring Data JPA are used for object-relational mapping and database operations.

### Main Database Entities

- User
- Role
- Employee
- Department
- Project
- Task
- Attendance
- LeaveRequest
- Payroll
- PerformanceReview
- EmployeeDocument
- Notification
- SupportTicket
- SupportTicketAttachment
- SupportTicketComment
- SupportTicketHistory
- TaskComment
- UserSettings
- PasswordResetOtp

The backend uses entity relationships and JPA repositories to manage application data.

---

## 10. Repository Structure

The GitHub repository contains two separate application directories: one for the Spring Boot backend and another for the Angular frontend.

**Repository Name:** Human-Resource-Management-System-Full-Stack-Application

**GitHub Repository:**

https://github.com/roshaniist/Human-Resource-Management-System-Full-Stack-Application

### 10.1 Main Repository Structure

```text
Human-Resource-Management-System-Full-Stack-Application/
│
├── emps/                    # Spring Boot Backend
│
├── emps_frontend/           # Angular Frontend
│
├── .env                     # Environment Configuration
│
├── docker-compose.yml       # Docker Compose Configuration
│
└── README.md                # Project Documentation
```

### 10.2 Backend Project Structure – `emps`

**Backend Technologies:** Java 21, Spring Boot, Spring Security, JWT, Hibernate, Spring Data JPA and PostgreSQL.

The backend follows a layered architecture.

```text
emps/
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/example/emps/
│   │   │       │
│   │   │       ├── config/
│   │   │       │   ├── CorsConfig.java
│   │   │       │   ├── DataInitializer.java
│   │   │       │   └── SecurityConfig.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AttendanceController.java
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── DashboardController.java
│   │   │       │   ├── DepartmentController.java
│   │   │       │   ├── EmployeeController.java
│   │   │       │   ├── EmployeeDocumentController.java
│   │   │       │   ├── LeaveRequestController.java
│   │   │       │   ├── NotificationController.java
│   │   │       │   ├── PayrollController.java
│   │   │       │   ├── PerformanceReviewController.java
│   │   │       │   ├── ProfileController.java
│   │   │       │   ├── ProjectController.java
│   │   │       │   ├── ReportController.java
│   │   │       │   ├── SettingsController.java
│   │   │       │   ├── SupportTicketController.java
│   │   │       │   ├── TaskController.java
│   │   │       │   └── UserController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── report/
│   │   │       │   │   ├── AttendanceReportDto.java
│   │   │       │   │   ├── DepartmentReportDto.java
│   │   │       │   │   ├── DocumentReportDto.java
│   │   │       │   │   ├── EmployeeReportDto.java
│   │   │       │   │   ├── EmployeeReportSummaryDto.java
│   │   │       │   │   ├── LeaveReportDto.java
│   │   │       │   │   ├── PayrollReportDto.java
│   │   │       │   │   ├── PerformanceReportDto.java
│   │   │       │   │   ├── ProjectReportDto.java
│   │   │       │   │   └── TaskReportDto.java
│   │   │       │   │
│   │   │       │   └── Other Request and Response DTOs
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Attendance.java
│   │   │       │   ├── Department.java
│   │   │       │   ├── Employee.java
│   │   │       │   ├── EmployeeDocument.java
│   │   │       │   ├── LeaveRequest.java
│   │   │       │   ├── Notification.java
│   │   │       │   ├── PasswordResetOtp.java
│   │   │       │   ├── Payroll.java
│   │   │       │   ├── PerformanceReview.java
│   │   │       │   ├── Project.java
│   │   │       │   ├── Role.java
│   │   │       │   ├── SupportTicket.java
│   │   │       │   ├── SupportTicketAttachment.java
│   │   │       │   ├── SupportTicketComment.java
│   │   │       │   ├── SupportTicketHistory.java
│   │   │       │   ├── Task.java
│   │   │       │   ├── TaskComment.java
│   │   │       │   ├── User.java
│   │   │       │   └── UserSettings.java
│   │   │       │
│   │   │       ├── enums/
│   │   │       │   ├── AttendanceStatus.java
│   │   │       │   ├── DocumentType.java
│   │   │       │   ├── LeaveStatus.java
│   │   │       │   ├── LeaveType.java
│   │   │       │   ├── NotificationType.java
│   │   │       │   ├── PayrollStatus.java
│   │   │       │   ├── PerformanceReviewStatus.java
│   │   │       │   ├── ProjectStatus.java
│   │   │       │   ├── RoleType.java
│   │   │       │   ├── TaskStatus.java
│   │   │       │   ├── TicketCategory.java
│   │   │       │   ├── TicketPriority.java
│   │   │       │   └── TicketStatus.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── BadRequestException.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── UnauthorizedException.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   └── JPA Repository Interfaces
│   │   │       │
│   │   │       ├── security/
│   │   │       │   ├── CustomUserDetailsService.java
│   │   │       │   ├── JwtAuthenticationEntryPoint.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── JwtService.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── impl/
│   │   │       │   │   └── Service Implementations
│   │   │       │   │
│   │   │       │   └── Service Interfaces
│   │   │       │
│   │   │       ├── specification/
│   │   │       │   └── EmployeeReportSpecification.java
│   │   │       │
│   │   │       └── EmpsApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.yml
│   │
│   └── test/
│
├── uploads/
├── target/
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── Dockerfile
```

### Backend Package Responsibilities

| Package | Responsibility |
|---|---|
| config | Application configuration, CORS and security configuration |
| controller | Handles incoming HTTP requests and REST APIs |
| dto | Transfers request and response data |
| entity | Represents database entities |
| enums | Defines fixed application values and statuses |
| exception | Handles application exceptions and errors |
| repository | Performs database operations |
| security | Implements JWT authentication and security components |
| service | Defines business service interfaces |
| service/impl | Implements business logic |
| specification | Handles dynamic database query specifications |
| resources | Contains application configuration and resources |

### 10.3 Frontend Project Structure – `emps_frontend`

**Frontend Technologies:** Angular, TypeScript, HTML, CSS and Bootstrap.

The frontend follows a component-based architecture.

```text
emps_frontend/
│
├── src/
│   │
│   ├── app/
│   │   │
│   │   ├── core/
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── role.guard.ts
│   │   │   │
│   │   │   ├── interceptors/
│   │   │   │   └── auth.interceptor.ts
│   │   │   │
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── employee.service.ts
│   │   │   │   ├── department.service.ts
│   │   │   │   ├── project.service.ts
│   │   │   │   ├── task.service.ts
│   │   │   │   ├── attendance.service.ts
│   │   │   │   ├── leave.service.ts
│   │   │   │   ├── payroll.service.ts
│   │   │   │   ├── performance.service.ts
│   │   │   │   └── document.service.ts
│   │   │   │
│   │   │   └── models/
│   │   │       ├── user.model.ts
│   │   │       ├── auth.model.ts
│   │   │       ├── employee.model.ts
│   │   │       ├── department.model.ts
│   │   │       ├── project.model.ts
│   │   │       ├── task.model.ts
│   │   │       ├── attendance.model.ts
│   │   │       ├── leave.model.ts
│   │   │       ├── payroll.model.ts
│   │   │       ├── performance.model.ts
│   │   │       └── document.model.ts
│   │   │
│   │   ├── layout/
│   │   │   ├── navbar/
│   │   │   ├── sidebar/
│   │   │   └── main-layout/
│   │   │
│   │   ├── pages/
│   │   │   │
│   │   │   ├── auth/
│   │   │   │   ├── login/
│   │   │   │   ├── register/
│   │   │   │   ├── forgot-password/
│   │   │   │   ├── verify-otp/
│   │   │   │   ├── reset-password/
│   │   │   │   └── change-password/
│   │   │   │
│   │   │   ├── dashboard/
│   │   │   │   └── dashboard/
│   │   │   │
│   │   │   ├── employees/
│   │   │   │   ├── employee-list/
│   │   │   │   ├── employee-view/
│   │   │   │   ├── employee-add/
│   │   │   │   └── employee-edit/
│   │   │   │
│   │   │   ├── departments/
│   │   │   │   ├── department-list/
│   │   │   │   ├── department-add/
│   │   │   │   └── department-edit/
│   │   │   │
│   │   │   ├── projects/
│   │   │   │   ├── project-list/
│   │   │   │   ├── project-add/
│   │   │   │   ├── project-edit/
│   │   │   │   └── project-view/
│   │   │   │
│   │   │   ├── tasks/
│   │   │   │   ├── task-list/
│   │   │   │   ├── task-add/
│   │   │   │   ├── task-edit/
│   │   │   │   └── task-view/
│   │   │   │
│   │   │   ├── attendance/
│   │   │   │   ├── attendance-list/
│   │   │   │   └── attendance-mark/
│   │   │   │
│   │   │   ├── leave/
│   │   │   │   ├── leave-list/
│   │   │   │   ├── leave-apply/
│   │   │   │   └── leave-view/
│   │   │   │
│   │   │   ├── payroll/
│   │   │   │   ├── payroll-list/
│   │   │   │   ├── payroll-add/
│   │   │   │   └── payroll-view/
│   │   │   │
│   │   │   ├── performance/
│   │   │   │   ├── review-list/
│   │   │   │   ├── review-add/
│   │   │   │   └── review-view/
│   │   │   │
│   │   │   ├── documents/
│   │   │   │   ├── document-list/
│   │   │   │   └── document-upload/
│   │   │   │
│   │   │   ├── reports/
│   │   │   │   └── reports/
│   │   │   │
│   │   │   └── profile/
│   │   │       └── profile/
│   │   │
│   │   ├── app.routes.ts
│   │   ├── app.config.ts
│   │   └── app.component.ts
│   │
│   ├── assets/
│   │   ├── images/
│   │   └── icons/
│   │
│   ├── styles.css
│   └── index.html
│
├── angular.json
├── package.json
├── tsconfig.json
└── README.md
```

### Frontend Folder Responsibilities

| Folder / File | Responsibility |
|---|---|
| core/guards | Protects application routes |
| core/interceptors | Adds JWT tokens to HTTP requests |
| core/services | Communicates with backend REST APIs |
| core/models | Defines TypeScript interfaces and data models |
| layout | Contains navbar, sidebar and main layout |
| pages/auth | Authentication and password management screens |
| pages/dashboard | Dashboard interface |
| pages/employees | Employee management screens |
| pages/departments | Department management screens |
| pages/projects | Project management screens |
| pages/tasks | Task management screens |
| pages/attendance | Attendance screens |
| pages/leave | Leave management screens |
| pages/payroll | Payroll screens |
| pages/performance | Performance review screens |
| pages/documents | Document management screens |
| pages/reports | Reporting screens |
| pages/profile | Profile management screens |
| assets | Images and icons |
| app.routes.ts | Application route definitions |
| app.config.ts | Angular application configuration |
| app.component.ts | Root Angular component |

---

## 11. Application Setup and Installation

### Prerequisites

Install the following software:

- Java 21
- Maven
- Node.js and npm
- Angular CLI
- PostgreSQL
- Git
- IntelliJ IDEA or Spring Tool Suite
- Visual Studio Code

### Step 1: Clone Repository

```bash
git clone https://github.com/roshaniist/Human-Resource-Management-System-Full-Stack-Application.git
```

### Step 2: Navigate to Project

```bash
cd Human-Resource-Management-System-Full-Stack-Application
```

### Step 3: Backend Configuration

Navigate to the backend folder:

```bash
cd emps
```

Configure the PostgreSQL database connection and other application properties in:

```text
src/main/resources/application.yml
```

Configure the required database credentials and JWT secret through your local environment configuration.

### Step 4: Run Backend

Using Maven:

```bash
mvn clean install
mvn spring-boot:run
```

Alternatively, on Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Step 5: Frontend Configuration

Open another terminal and navigate to the frontend directory:

```bash
cd emps_frontend
```

Install dependencies:

```bash
npm install
```

### Step 6: Run Angular Application

```bash
ng serve
```

The frontend runs on:

```text
http://localhost:4200
```

---

## 12. Docker Deployment

The project uses Docker and Docker Compose to containerize and manage application services.

### Prerequisites

- Git
- Docker Desktop
- Docker Compose

### Step 1: Clone Repository

```bash
git clone https://github.com/roshaniist/Human-Resource-Management-System-Full-Stack-Application.git
```

### Step 2: Navigate to Project

```bash
cd Human-Resource-Management-System-Full-Stack-Application
```

### Step 3: Configure Environment Variables

Create or configure a local `.env` file using the variables required by `docker-compose.yml`.

Example only:

```env
DB_NAME=your_database_name
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret
```

Use the actual environment variable names expected by your Docker and Spring Boot configuration.

**Security:** Never commit database passwords, JWT secrets or other sensitive credentials to GitHub.

### Step 4: Build and Start Services

```bash
docker compose up -d --build
```

### Step 5: Check Running Containers

```bash
docker compose ps
```

### Step 6: View Logs

```bash
docker compose logs -f
```

Backend logs:

```bash
docker compose logs -f backend
```

Frontend logs:

```bash
docker compose logs -f frontend
```

Use the actual service names configured in `docker-compose.yml`.

### Step 7: Stop Services

```bash
docker compose down
```

To remove associated volumes as well:

```bash
docker compose down -v
```

**Warning:** Removing volumes can permanently delete database data.

---

## 13. Docker Ports & Application URLs

The EPMS application uses Docker Compose to run the frontend, backend, and PostgreSQL database services.

### Docker Port Configuration

| Service | Technology | Host Port | Container Port | Local URL |
|---|---|---:|---:|---|
| Frontend | Angular | 4200 | 4200 | http://localhost:4200 |
| Backend | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Database | PostgreSQL | 5432 | 5432 | localhost:5432 |

### Docker Commands

**Build and Start Containers**

```bash
docker compose up -d --build
```

**Check Running Containers**

```bash
docker compose ps
```

**View Application Logs**

```bash
docker compose logs -f
```

**Stop Containers**

```bash
docker compose down
```

**Stop Containers and Remove Volumes**

```bash
docker compose down -v
```

*Note: The port mappings shown above assume your docker-compose.yml uses these host-to-container mappings. Verify them against your actual Docker configuration.*

---

## 14. Author

**Roshan Kumar**

Bachelor of Technology (B.Tech) – Computer Science and Engineering.

GitHub: [https://github.com/roshaniist](https://github.com/roshaniist)

**Project Repository:**

[Human-Resource-Management-System-Full-Stack-Application](https://github.com/roshaniist/Human-Resource-Management-System-Full-Stack-Application)

---

## 15. License

This project is currently available for viewing on GitHub.

No open-source license has been specified yet. All rights remain with the project author unless a license is added.

---

**Enterprise Project Management System (EPMS)**

*Built with Java 21, Spring Boot, Angular, PostgreSQL, Spring Security, JWT and Docker.*
