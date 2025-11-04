package com.example.projectsubmission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectsubmission.model.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent(String student);
    List<Submission> findByProjectId(Long projectId);
}


