package com.college.library.service;

import com.college.library.entity.Student;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student registerStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Student with email " + student.getEmail() + " already exists.");
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);
        if (studentRepository.existsByEmailAndStudentIdNot(updated.getEmail(), id)) {
            throw new DuplicateResourceException("Email already in use: " + updated.getEmail());
        }
        existing.setStudentName(updated.getStudentName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setPhoneNumber(updated.getPhoneNumber());
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        Student existing = getStudentById(id);
        studentRepository.delete(existing);
    }
}