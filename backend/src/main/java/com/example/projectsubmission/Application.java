package com.example.projectsubmission;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.projectsubmission.model.User;
import com.example.projectsubmission.model.UserRole;
import com.example.projectsubmission.repository.UserRepository;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", "admin", UserRole.Admin));
            }
            if (userRepository.findByUsername("teacher1").isEmpty()) {
                userRepository.save(new User("teacher1", "teach123", UserRole.Teacher));
            }
            if (userRepository.findByUsername("student1").isEmpty()) {
                userRepository.save(new User("student1", "stud123", UserRole.Student));
            }
        };
    }
}


