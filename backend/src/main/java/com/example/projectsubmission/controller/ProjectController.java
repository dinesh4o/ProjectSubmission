package com.example.projectsubmission.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectsubmission.model.Project;
import com.example.projectsubmission.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> list(@RequestParam(required = false) String teacher) {
        if (teacher != null && !teacher.isBlank()) {
            return projectService.findByTeacher(teacher);
        }
        return projectService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        try {
            String teacher = body.get("teacher");
            String title = body.get("title");
            String description = body.getOrDefault("description", "");
            
            if (teacher == null || teacher.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher is required"));
            }
            if (title == null || title.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            
            Project p = new Project(teacher, title, description);
            Project created = projectService.create(p);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create project: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}


