package com.college.library.service;

import com.college.library.entity.*;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.BookRepository;
import com.college.library.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    public BookIssueService(BookIssueRepository issueRepository,
                            StudentRepository studentRepository,
                            BookRepository bookRepository) {
        this.issueRepository = issueRepository;
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookIssue issueBook(Long studentId, Long bookId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("No available copies for book: " + book.getTitle());
        }

        long activeIssues = issueRepository.countByStudentStudentIdAndStatus(studentId, IssueStatus.ISSUED);
        if (activeIssues >= 3) {
            throw new BusinessRuleException("Student has already reached the maximum limit of 3 active book issues.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookIssue issue = new BookIssue();
        issue.setStudent(student);
        issue.setBook(book);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14));
        issue.setStatus(IssueStatus.ISSUED);

        return issueRepository.save(issue);
    }

    @Transactional
    public BookIssue returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with ID: " + issueId));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new BusinessRuleException("This book has already been returned.");
        }

        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return issueRepository.save(issue);
    }

    public List<BookIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<BookIssue> getIssuesByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return issueRepository.findByStudentStudentId(studentId);
    }

    public List<BookIssue> getOverdueIssues() {
        return issueRepository.findByStatusAndDueDateBefore(IssueStatus.ISSUED, LocalDate.now());
    }
}