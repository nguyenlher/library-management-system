package com.library.borrow_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.library.borrow_service.entity.Borrow;

public class BorrowWithFineDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Borrow.BorrowStatus status;
    private BigDecimal totalFine;

    // Constructors
    public BorrowWithFineDTO() {}

    public BorrowWithFineDTO(Borrow borrow, BigDecimal totalFine, String bookTitle) {
        this.id = borrow.getId();
        this.userId = borrow.getUserId();
        this.bookId = borrow.getBookId();
        this.bookTitle = bookTitle;
        this.borrowDate = borrow.getBorrowDate();
        this.dueDate = borrow.getDueDate();
        this.returnDate = borrow.getReturnDate();
        this.status = borrow.getStatus();
        this.totalFine = totalFine;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
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

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Borrow.BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(Borrow.BorrowStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(BigDecimal totalFine) {
        this.totalFine = totalFine;
    }
}