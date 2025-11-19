package com.library.book_service.repository;

import com.library.book_service.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    List<BookCategory> findByBookId(Long bookId);

    List<BookCategory> findByCategoryId(Long categoryId);

    void deleteByBookId(Long bookId);

    void deleteByCategoryId(Long categoryId);
}