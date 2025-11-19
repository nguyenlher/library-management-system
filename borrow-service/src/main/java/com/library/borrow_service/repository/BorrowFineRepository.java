package com.library.borrow_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.borrow_service.entity.BorrowFine;

@Repository
public interface BorrowFineRepository extends JpaRepository<BorrowFine, Long> {

    List<BorrowFine> findByBorrowId(Long borrowId);

    List<BorrowFine> findByUserId(Long userId);

    List<BorrowFine> findByUserIdAndPaid(Long userId, Boolean paid);

    List<BorrowFine> findByReason(BorrowFine.FineReason reason);

    List<BorrowFine> findByPaid(Boolean paid);
}