package com.example.emps.specification;

import com.example.emps.entity.Employee;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class EmployeeReportSpecification {

    private EmployeeReportSpecification() {
    }

    public static Specification<Employee> filter(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    ) {
        return (root, query, criteriaBuilder) -> {

            Join<Object, Object> user =
                    root.join("user", JoinType.LEFT);

            Join<Object, Object> department =
                    root.join("department", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String value = "%" + search.trim().toLowerCase() + "%";

                Predicate employeeCode =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("employeeCode")),
                                value
                        );

                Predicate firstName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(user.get("firstName")),
                                value
                        );

                Predicate lastName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(user.get("lastName")),
                                value
                        );

                Predicate email =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(user.get("email")),
                                value
                        );

                Predicate phone =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("phoneNumber")),
                                value
                        );

                Predicate fullName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        criteriaBuilder.concat(
                                                criteriaBuilder.concat(
                                                        user.get("firstName"),
                                                        " "
                                                ),
                                                user.get("lastName")
                                        )
                                ),
                                value
                        );

                predicates.add(
                        criteriaBuilder.or(
                                employeeCode,
                                firstName,
                                lastName,
                                fullName,
                                email,
                                phone
                        )
                );
            }

            if (departmentId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                department.get("id"),
                                departmentId
                        )
                );
            }

            if (designation != null && !designation.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("designation")
                                ),
                                designation.trim().toLowerCase()
                        )
                );
            }

            if (fromDate != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("joiningDate"),
                                fromDate
                        )
                );
            }

            if (toDate != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("joiningDate"),
                                toDate
                        )
                );
            }

            if (active != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                user.get("active"),
                                active
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}