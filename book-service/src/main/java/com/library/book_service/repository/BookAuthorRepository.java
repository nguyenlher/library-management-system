package com.library.book_service.repository;

import com.library.book_service.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {

    List<BookAuthor> findByBookId(Long bookId);

    List<BookAuthor> findByAuthorId(Long authorId);

    void deleteByBookId(Long bookId);

    void deleteByAuthorId(Long authorId);
}