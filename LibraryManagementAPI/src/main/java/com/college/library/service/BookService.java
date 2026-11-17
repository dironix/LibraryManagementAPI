package com.college.library.service;

import com.college.library.entity.Book;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new BusinessRuleException("Available copies cannot exceed total copies.");
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }

    public Book updateBook(Long id, Book updated) {
        Book existing = getBookById(id);
        if (bookRepository.existsByIsbnAndBookIdNot(updated.getIsbn(), id)) {
            throw new DuplicateResourceException("Another book already uses ISBN: " + updated.getIsbn());
        }
        if (updated.getAvailableCopies() > updated.getTotalCopies()) {
            throw new BusinessRuleException("Available copies cannot exceed total copies.");
        }
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setIsbn(updated.getIsbn());
        existing.setCategory(updated.getCategory());
        existing.setTotalCopies(updated.getTotalCopies());
        existing.setAvailableCopies(updated.getAvailableCopies());
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        Book existing = getBookById(id);
        bookRepository.delete(existing);
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }
}