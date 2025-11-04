package com.example.projectsubmission.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectsubmission.model.Submission;
import com.example.projectsubmission.service.SubmissionService;
import com.example.projectsubmission.storage.FileStorageService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;
    private final FileStorageService fileStorageService;

    @Value("${app.base-url:}")
    private String baseUrl; // e.g., https://your-backend.onrender.com

    public SubmissionController(SubmissionService submissionService, FileStorageService fileStorageService) {
        this.submissionService = submissionService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Submission> list(@RequestParam(required = false) String student,
                                 @RequestParam(required = false) Long projectId) {
        if (student != null && !student.isBlank()) return submissionService.findByStudent(student);
        if (projectId != null) return submissionService.findByProject(projectId);
        return submissionService.findAll();
    }

    @PostMapping
    public ResponseEntity<Submission> upload(@RequestParam Long projectId,
                                             @RequestParam String student,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        String storedName = fileStorageService.store(file, student);
        String fileUrl = (baseUrl != null && !baseUrl.isBlank() ? baseUrl : "") + "/files/" + storedName;
        Submission created = submissionService.create(student, projectId, file.getOriginalFilename(), fileUrl);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { submissionService.delete(id); }
}


