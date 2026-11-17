package com.college.library.controller;

import com.college.library.entity.BookIssue;
import com.college.library.service.BookIssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    private final BookIssueService issueService;

    public BookIssueController(BookIssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/{studentId}/{bookId}")
    public ResponseEntity<BookIssue> issueBook(@PathVariable Long studentId, @PathVariable Long bookId) {
        return new ResponseEntity<>(issueService.issueBook(studentId, bookId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookIssue>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BookIssue>> getIssuesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(issueService.getIssuesByStudent(studentId));
    }

    @PutMapping("/return/{issueId}")
    public ResponseEntity<BookIssue> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(issueService.returnBook(issueId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BookIssue>> getOverdueIssues() {
        return ResponseEntity.ok(issueService.getOverdueIssues());
    }
}