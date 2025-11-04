package com.example.projectsubmission.controller;

import java.util.List;
import java.util.Map;

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
    public Project create(@RequestBody Map<String, String> body) {
        Project p = new Project(body.get("teacher"), body.get("title"), body.get("description"));
        return projectService.create(p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}


