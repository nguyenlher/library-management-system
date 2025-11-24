package com.library.borrow_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.borrow_service.entity.Borrow;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserId(Long userId);

    List<Borrow> findByBookId(Long bookId);

    List<Borrow> findByStatus(Borrow.BorrowStatus status);

    List<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, Borrow.BorrowStatus status);

    @Query("SELECT b FROM Borrow b WHERE b.dueDate < :currentDate AND b.status = :status")
    List<Borrow> findOverdueBorrows(@Param("currentDate") LocalDateTime currentDate,
                                   @Param("status") Borrow.BorrowStatus status);

    @Query("SELECT b FROM Borrow b WHERE b.userId = :userId AND b.status = :status")
    List<Borrow> findActiveBorrowsByUser(@Param("userId") Long userId,
                                        @Param("status") Borrow.BorrowStatus status);

    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.userId = :userId AND b.status = :status")
    Long countActiveBorrowsByUser(@Param("userId") Long userId,
                                 @Param("status") Borrow.BorrowStatus status);

    Long countByBookId(Long bookId);
}