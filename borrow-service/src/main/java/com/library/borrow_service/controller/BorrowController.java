package com.library.borrow_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.borrow_service.dto.BorrowWithFineDTO;
import com.library.borrow_service.entity.Borrow;
import com.library.borrow_service.repository.BorrowFineRepository;
import com.library.borrow_service.repository.BorrowRepository;

@RestController
@RequestMapping("/borrows")
@CrossOrigin(origins = "*")
public class BorrowController {

    private final BorrowRepository borrowRepository;
    private final BorrowFineRepository borrowFineRepository;

    public BorrowController(BorrowRepository borrowRepository, BorrowFineRepository borrowFineRepository) {
        this.borrowRepository = borrowRepository;
        this.borrowFineRepository = borrowFineRepository;
    }

    @GetMapping
    public ResponseEntity<List<Borrow>> getAllBorrows() {
        List<Borrow> borrows = borrowRepository.findAll();
        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowWithFineDTO>> getBorrowsByUser(@PathVariable Long userId) {
        List<Borrow> borrows = borrowRepository.findByUserId(userId);

        List<BorrowWithFineDTO> borrowWithFineDTOs = borrows.stream()
            .map(borrow -> {
                BigDecimal calculatedFine = calculateFine(borrow);
                return new BorrowWithFineDTO(borrow, calculatedFine);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(borrowWithFineDTOs);
    }

    private BigDecimal calculateFine(Borrow borrow) {
        // Calculate fine based on return date vs due date
        if (borrow.getReturnDate() != null && borrow.getReturnDate().isAfter(borrow.getDueDate())) {
            // Calculate number of days overdue
            long daysOverdue = java.time.Duration.between(borrow.getDueDate(), borrow.getReturnDate()).toDays();
            if (daysOverdue > 0) {
                // 10,000 VND per day overdue
                return BigDecimal.valueOf(daysOverdue * 10000);
            }
        }

        // Check for additional fines from borrow_fines table (lost, damage, etc.)
        List<com.library.borrow_service.entity.BorrowFine> fines = borrowFineRepository.findByBorrowId(borrow.getId());
        return fines.stream()
            .map(com.library.borrow_service.entity.BorrowFine::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PostMapping
    public ResponseEntity<Borrow> createBorrow(@RequestBody CreateBorrowRequest request) {
        System.out.println("BorrowController: Received create borrow request for userId=" + request.getUserId() + ", bookId=" + request.getBookId());
        
        // Check if user already has an active borrow for this book
        List<Borrow> existingBorrows = borrowRepository.findByUserIdAndBookIdAndStatus(
            request.getUserId(), 
            request.getBookId(), 
            Borrow.BorrowStatus.BORROWED
        );
        
        if (!existingBorrows.isEmpty()) {
            System.out.println("BorrowController: Found existing borrow record, returning existing: " + existingBorrows.get(0).getId());
            // Return the existing borrow record instead of creating a new one
            return ResponseEntity.ok(existingBorrows.get(0));
        }

        System.out.println("BorrowController: Creating new borrow record");
        Borrow borrow = new Borrow();
        borrow.setUserId(request.getUserId());
        borrow.setBookId(request.getBookId());
        borrow.setBorrowDate(request.getBorrowDate() != null ? request.getBorrowDate() : LocalDateTime.now());
        borrow.setDueDate(request.getDueDate() != null ? request.getDueDate() : LocalDateTime.now().plusDays(14));
        borrow.setStatus(Borrow.BorrowStatus.BORROWED);

        Borrow savedBorrow = borrowRepository.save(borrow);
        System.out.println("BorrowController: Created new borrow record with id: " + savedBorrow.getId());
        return ResponseEntity.ok(savedBorrow);
    }

    @PutMapping("/{borrowId}/return")
    public ResponseEntity<Borrow> markReturned(@PathVariable Long borrowId) {
        return borrowRepository.findById(borrowId)
            .map(borrow -> {
                borrow.setReturnDate(LocalDateTime.now());
                borrow.setStatus(LocalDateTime.now().isAfter(borrow.getDueDate()) ? Borrow.BorrowStatus.LATE_RETURNED : Borrow.BorrowStatus.RETURNED);
                Borrow updated = borrowRepository.save(borrow);
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{borrowId}")
    public ResponseEntity<Void> deleteBorrow(@PathVariable Long borrowId) {
        if (borrowRepository.existsById(borrowId)) {
            borrowRepository.deleteById(borrowId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static class CreateBorrowRequest {
        private Long userId;
        private Long bookId;
        private LocalDateTime borrowDate;
        private LocalDateTime dueDate;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public LocalDateTime getBorrowDate() {
            return borrowDate;
        }

        public void setBorrowDate(LocalDateTime borrowDate) {
            this.borrowDate = borrowDate;
        }

        public LocalDateTime getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
        }
    }
}
