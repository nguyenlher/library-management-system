package com.library.book_service.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.book_service.dto.AuthorDTO;
import com.library.book_service.dto.BookDTO;
import com.library.book_service.dto.CategoryDTO;
import com.library.book_service.entity.Author;
import com.library.book_service.entity.Book;
import com.library.book_service.repository.AuthorRepository;
import com.library.book_service.repository.BookRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).stream()
                .findFirst()
                .map(this::convertToDTO);
    }

    public List<BookDTO> getBooksByPublisher(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(bookDTO.getTitle());
                    existingBook.setIsbn(bookDTO.getIsbn());
                    existingBook.setSummary(bookDTO.getSummary());
                    existingBook.setPublisherId(bookDTO.getPublisherId());
                    existingBook.setPublishYear(bookDTO.getPublishYear());
                    existingBook.setEdition(bookDTO.getEdition());
                    existingBook.setCoverImageUrl(bookDTO.getCoverImageUrl());
                    existingBook.setBorrowFee(bookDTO.getBorrowFee());
                    existingBook.setTotalCopies(bookDTO.getTotalCopies());
                    existingBook.setAvailableCopies(bookDTO.getAvailableCopies());
                    
                    // Update authors
                    if (bookDTO.getAuthorIds() != null) {
                        Set<Author> authors = bookDTO.getAuthorIds().stream()
                                .map(authorId -> authorRepository.findById(authorId).orElse(null))
                                .filter(author -> author != null)
                                .collect(Collectors.toSet());
                        existingBook.setAuthors(authors);
                    }
                    
                    Book updatedBook = bookRepository.save(existingBook);
                    return convertToDTO(updatedBook);
                });
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setSummary(bookDTO.getSummary());
        book.setPublisherId(bookDTO.getPublisherId());
        book.setPublishYear(bookDTO.getPublishYear());
        book.setEdition(bookDTO.getEdition());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());
        book.setBorrowFee(bookDTO.getBorrowFee());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        
        // Set authors
        if (bookDTO.getAuthorIds() != null) {
            Set<Author> authors = bookDTO.getAuthorIds().stream()
                    .map(authorId -> authorRepository.findById(authorId).orElse(null))
                    .filter(author -> author != null)
                    .collect(Collectors.toSet());
            book.setAuthors(authors);
        }
        
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public boolean isBookAvailable(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.isPresent() && book.get().getAvailableCopies() > 0;
    }

    public boolean borrowBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setSummary(book.getSummary());
        dto.setPublisherId(book.getPublisherId());
        dto.setPublishYear(book.getPublishYear());
        dto.setEdition(book.getEdition());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setBorrowFee(book.getBorrowFee());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());

        // Convert authors and categories if needed
        if (book.getAuthors() != null) {
            dto.setAuthors(book.getAuthors().stream()
                    .map(author -> new AuthorDTO(author.getId(), author.getName()))
                    .collect(Collectors.toSet()));
        }
        if (book.getCategories() != null) {
            dto.setCategories(book.getCategories().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toSet()));
        }
        if (book.getPublisher() != null) {
            dto.setPublisherName(book.getPublisher().getName());
        }

        return dto;
    }
}