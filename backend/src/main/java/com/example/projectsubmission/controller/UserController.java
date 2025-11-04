package com.example.projectsubmission.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectsubmission.model.User;
import com.example.projectsubmission.model.UserRole;
import com.example.projectsubmission.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> all() { return userService.findAll(); }

    @PostMapping
    public User create(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        UserRole role = UserRole.valueOf(body.get("role"));
        return userService.create(username, password, role);
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> update(@PathVariable String username, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        UserRole role = body.get("role") != null ? UserRole.valueOf(body.get("role")) : null;
        return userService.update(username, password, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username) { userService.delete(username); }
}


