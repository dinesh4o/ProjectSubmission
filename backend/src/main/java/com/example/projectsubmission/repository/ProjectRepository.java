package com.example.projectsubmission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectsubmission.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeacher(String teacher);
}


