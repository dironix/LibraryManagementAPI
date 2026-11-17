package com.college.library.repository;

import com.college.library.entity.BookIssue;
import com.college.library.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByStudentStudentId(Long studentId);
    long countByStudentStudentIdAndStatus(Long studentId, IssueStatus status);
    List<BookIssue> findByStatusAndDueDateBefore(IssueStatus status, LocalDate date);
    boolean existsByBookBookIdAndStatus(Long bookId, IssueStatus status);
}