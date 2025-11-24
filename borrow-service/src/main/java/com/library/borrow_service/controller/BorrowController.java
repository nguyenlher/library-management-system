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
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    public BorrowController(BorrowRepository borrowRepository, BorrowFineRepository borrowFineRepository, RestTemplate restTemplate) {
        this.borrowRepository = borrowRepository;
        this.borrowFineRepository = borrowFineRepository;
        this.restTemplate = restTemplate;
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
                String bookTitle = getBookTitle(borrow.getBookId());
                return new BorrowWithFineDTO(borrow, calculatedFine, bookTitle);
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

    private String getBookTitle(Long bookId) {
        try {
            String url = "http://localhost:8082/books/" + bookId;
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Assuming the response is a map with "title" key
                if (response.getBody() instanceof java.util.Map) {
                    java.util.Map<String, Object> book = (java.util.Map<String, Object>) response.getBody();
                    return (String) book.get("title");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching book title for bookId " + bookId + ": " + e.getMessage());
        }
        return "N/A";
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
        borrow.setBorrowDate(request.getBorrowDate() != null ? java.time.LocalDate.parse(request.getBorrowDate()).atStartOfDay() : LocalDateTime.now());
        borrow.setDueDate(request.getDueDate() != null ? java.time.LocalDate.parse(request.getDueDate()).atStartOfDay() : LocalDateTime.now().plusDays(14));
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

    @PutMapping("/{borrowId}")
    public ResponseEntity<Borrow> updateBorrow(@PathVariable Long borrowId, @RequestBody UpdateBorrowRequest request) {
        return borrowRepository.findById(borrowId)
            .map(borrow -> {
                if (request.getUserId() != null) {
                    borrow.setUserId(request.getUserId());
                }
                if (request.getBookId() != null) {
                    borrow.setBookId(request.getBookId());
                }
                if (request.getBorrowDate() != null) {
                    borrow.setBorrowDate(java.time.LocalDate.parse(request.getBorrowDate()).atStartOfDay());
                }
                if (request.getDueDate() != null) {
                    borrow.setDueDate(java.time.LocalDate.parse(request.getDueDate()).atStartOfDay());
                }
                if (request.getStatus() != null) {
                    borrow.setStatus(Borrow.BorrowStatus.valueOf(request.getStatus()));
                }
                if (request.getReturnDate() != null) {
                    borrow.setReturnDate(java.time.LocalDate.parse(request.getReturnDate()).atStartOfDay());
                }
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
        private String borrowDate;
        private String dueDate;

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

        public String getBorrowDate() {
            return borrowDate;
        }

        public void setBorrowDate(String borrowDate) {
            this.borrowDate = borrowDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }
    }

    public static class UpdateBorrowRequest {
        private Long userId;
        private Long bookId;
        private String borrowDate;
        private String dueDate;
        private String status;
        private String returnDate;

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

        public String getBorrowDate() {
            return borrowDate;
        }

        public void setBorrowDate(String borrowDate) {
            this.borrowDate = borrowDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(String returnDate) {
            this.returnDate = returnDate;
        }
    }
}
