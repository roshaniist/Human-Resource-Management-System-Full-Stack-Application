package com.example.emps.config;

import com.example.emps.entity.Role;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.RoleRepository;
import com.example.emps.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class
DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create all roles if they do not already exist
            for (RoleType roleType : RoleType.values()) {

                if (roleRepository.findByRoleName(roleType).isEmpty()) {

                    Role role = new Role();
                    role.setRoleName(roleType);

                    roleRepository.save(role);
                }
            }

            // Create one default ADMIN user
            String adminEmail = "admin@emps.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                Role adminRole = roleRepository
                        .findByRoleName(RoleType.ADMIN)
                        .orElseThrow(() ->
                                new RuntimeException("ADMIN role not found"));

                User admin = new User();

                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(
                        passwordEncoder.encode("Admin@12345")
                );
                admin.setRole(adminRole);
                admin.setActive(true);

                userRepository.save(admin);
            }
        };
    }
}

//  it checks whether predefined roles already exit, and if a role is missing, it creates and saves it
// this is used to prevent duplicates roles entries
// it is used to insert default or initial data into the database automatically when application start