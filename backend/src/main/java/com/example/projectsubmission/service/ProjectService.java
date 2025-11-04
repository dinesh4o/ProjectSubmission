package com.example.projectsubmission.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projectsubmission.model.Project;
import com.example.projectsubmission.repository.ProjectRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> findByTeacher(String teacher) {
        return projectRepository.findByTeacher(teacher);
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}


