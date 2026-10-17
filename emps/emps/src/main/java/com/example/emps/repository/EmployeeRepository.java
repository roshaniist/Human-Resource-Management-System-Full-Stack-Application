package com.example.emps.repository;

import com.example.emps.entity.Employee;
import com.example.emps.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmployeeCode(
            String employeeCode
    );

    boolean existsByEmployeeCode(
            String employeeCode
    );

    Optional<Employee> findByUserId(
            Long userId
    );

    Optional<Employee> findByUserEmail(
            String email
    );

    List<Employee> findByDepartmentId(
            Long departmentId
    );

    long countByDepartmentId(
            Long departmentId
    );

    boolean existsByIdAndDepartmentManagerId(
            Long employeeId,
            Long managerId
    );

    List<Employee> findByUserRoleRoleName(
            RoleType roleName
    );
}