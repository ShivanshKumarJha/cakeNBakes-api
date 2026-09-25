package com.shivansh.cakes.common.config;

import com.shivansh.cakes.admin.entity.Admin;
import com.shivansh.cakes.admin.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.count() == 0) {
                Admin admin = Admin.builder()
                        .email("admin@cakesandbakes.com")
                        .password(passwordEncoder.encode("admin123"))
                        .createdAt(LocalDateTime.now())
                        .build();
                adminRepository.save(admin);
                System.out.println("Default Admin created: admin@cakesandbakes.com / admin123");
            }
        };
    }
}
