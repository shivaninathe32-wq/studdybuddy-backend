package com.studyflow.controller;

import com.studyflow.dto.assignment.AssignmentRequest;
import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.entity.SubmissionStatus;
import com.studyflow.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    public Page<AssignmentResponse> list(
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(required = false) Boolean upcoming,
            @RequestParam(required = false) Boolean submitted,
            @RequestParam(required = false) Boolean overdue,
            @PageableDefault(size = 20, sort = "dueDateTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return assignmentService.list(status, upcoming, submitted, overdue, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse create(@Valid @RequestBody AssignmentRequest request) {
        return assignmentService.create(request);
    }

    @PutMapping("/{id}")
    public AssignmentResponse update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assignmentService.delete(id);
    }
}
