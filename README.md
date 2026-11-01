# Enterprise Project Management System (EPMS)

A Full-Stack Enterprise Project Management System built using **Java Spring Boot, Angular, Spring Security, JWT Authentication, PostgreSQL, and Docker**.

EPMS is designed to simplify and manage employee information, departments, projects, tasks, attendance, leave requests, payroll, performance reviews, documents, reports, and IT support activities through a centralized web application.

---

## Project Overview

The Enterprise Project Management System (EPMS) is a web-based application that helps organizations manage their employees and daily business operations efficiently.

The application provides role-based access control so that users can access features according to their assigned roles.

It follows a full-stack architecture with Angular on the frontend, Spring Boot on the backend, and PostgreSQL as the database. Docker and Docker Compose are used to run the application services.

## Key Features & Modules

### 1. Authentication & Authorization
- User Registration
- User Login
- Forgot Password
- JWT-based Authentication
- Spring Security Integration
- Role-Based Access Control

### 2. Employee Management
- Add, View, Update and Manage Employees
- Employee Profile Management
- Employee Information and Status

### 3. Department Management
- Create and Manage Departments
- Assign Employees to Departments
- Department Information Management

### 4. Project Management
- Create and Manage Projects
- Assign Project Managers
- Assign Employees to Projects
- Track Project Status

### 5. Task Management
- Create and Assign Tasks
- Track Task Status
- Manage Task Information
- Monitor Task Progress

### 6. Attendance Management
- Employee Check-In and Check-Out
- Break Start and End
- Attendance History
- Working Hours Calculation
- Attendance Status Tracking

### 7. Leave Management
- Apply for Leave
- View Leave History
- Approve or Reject Leave Requests
- Leave Status Tracking
- Rejection Reason Management

### 8. Payroll Management
- Generate and Manage Payroll
- Basic Salary and Allowances
- Bonus and Deductions
- Gross Salary and Net Salary
- Payroll Status Tracking

### 9. Performance Management
- Employee Performance Reviews
- Performance Tracking
- Review Information Management

### 10. Document Management
- Upload Employee Documents
- Manage Profile Photos
- View and Download Documents

### 11. Reports
- Employee Reports
- Department Reports
- Employee Summary and Statistics

### 12. IT Support
- Create Support Tickets
- Assign Support Tickets
- Track Ticket Status
- Manage IT Support Activities

### 13. Notification Management
- Automatic Notifications
- Read and Unread Notifications
- Mark Notifications as Read
- Notification History

---

## Technology Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot |
| Security | Spring Security, JWT |
| Backend Architecture | REST APIs |
| ORM | Hibernate / Spring Data JPA |
| Frontend | Angular, TypeScript |
| UI | HTML, CSS, Bootstrap |
| Database | PostgreSQL |
| Build Tool | Maven |
| Containerization | Docker |
| Container Management | Docker Compose |
| Version Control | Git and GitHub |

---

## Project Architecture

The application follows a three-tier architecture.

**1. Frontend Layer**
- Developed using Angular.
- Provides the user interface.
- Communicates with the backend through REST APIs.

**2. Backend Layer**
- Developed using Java and Spring Boot.
- Handles business logic and API requests.
- Uses Spring Security and JWT for authentication and authorization.
- Uses Hibernate/JPA for database operations.

**3. Database Layer**
- PostgreSQL stores application data.
- Stores employee, department, project, task, attendance, leave, payroll, and other application records.

---

## Role-Based Access Control

The application provides different access permissions based on user roles.

Roles include:

- ADMIN
- HR
- MANAGER
- EMPLOYEE

Each role has access to the features and operations permitted by the application's authorization rules.

---

## Docker Setup

The project uses Docker Compose to manage the application services.

### Prerequisites

Install the following tools:

- Git
- Docker Desktop
- Docker Compose

### Clone Repository

```bash
git clone https://github.com/roshaniist/Human-Resource-Management-System-Full-Stack-Application.git
```

### Navigate to Project Directory

```bash
cd Human-Resource-Management-System-Full-Stack-Application
```

### Configure Environment Variables

Create a `.env` file in the project root directory using the variables required by `docker-compose.yml`.

Configure database credentials, JWT secrets, and other application settings.

Do not commit the `.env` file to GitHub.

### Run Application

```bash
docker compose up -d
```

### Check Running Containers

```bash
docker compose ps
```

### View Application Logs

```bash
docker compose logs -f
```

### Stop Application

```bash
docker compose down
```

---

## Application URLs

When running with the current local Docker port configuration:

| Service | URL |
|---|---|
| Angular Frontend | http://localhost:4200 |
| Spring Boot Backend | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

---

## Project Structure

```text
Human-Resource-Management-System-Full-Stack-Application/
│
├── emps/                       # Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   ├── pom.xml
│   └── Dockerfile
│
├── emps_frontend/              # Angular Frontend
│   ├── src/
│   │   └── app/
│   ├── package.json
│   ├── angular.json
│   └── Dockerfile
│
├── docker-compose.yml
├── .dockerignore
├── .gitignore
├── .env                        # Local environment configuration
└── README.md
```

---

## Future Enhancements

- Additional reporting and analytics features.
- Improved dashboard visualizations.
- Further automation of HR operations.
- Additional application integrations.

---

## Author

**Roshan Kumar**

GitHub: https://github.com/roshaniist

---

## License

This project is currently available for viewing on GitHub. No open-source license has been specified yet.
