package com.example.projectsubmission.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projectsubmission.model.Submission;
import com.example.projectsubmission.repository.SubmissionRepository;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public List<Submission> findAll() { return submissionRepository.findAll(); }
    public List<Submission> findByStudent(String student) { return submissionRepository.findByStudent(student); }
    public List<Submission> findByProject(Long projectId) { return submissionRepository.findByProjectId(projectId); }

    public Submission create(String student, Long projectId, String fileName, String fileUrl) {
        Submission s = new Submission(student, projectId, fileName, fileUrl, OffsetDateTime.now());
        return submissionRepository.save(s);
    }

    public void delete(Long id) { submissionRepository.deleteById(id); }
}


