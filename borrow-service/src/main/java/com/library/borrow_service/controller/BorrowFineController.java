package com.library.borrow_service.controller;

import java.math.BigDecimal;
import java.util.List;

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

import com.library.borrow_service.dto.BorrowFineDTO;
import com.library.borrow_service.entity.BorrowFine;
import com.library.borrow_service.service.BorrowService;

@RestController
@RequestMapping("/fines")
@CrossOrigin(origins = "*")
public class BorrowFineController {

    private final BorrowService borrowService;

    public BorrowFineController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    public ResponseEntity<List<BorrowFineDTO>> listFines() {
        return ResponseEntity.ok(borrowService.getAllFines());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowFineDTO>> getFinesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowService.getFinesByUser(userId));
    }

    @GetMapping("/{fineId}")
    public ResponseEntity<BorrowFineDTO> getFine(@PathVariable Long fineId) {
        BorrowFineDTO fine = borrowService.getFineById(fineId);
        if (fine != null) {
            return ResponseEntity.ok(fine);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BorrowFineDTO> createFine(@RequestBody CreateFineRequest request) {
        BorrowFineDTO fine = borrowService.createFine(request.getBorrowId(), request.getUserId(), request.getAmount(), request.getReason());
        return ResponseEntity.ok(fine);
    }

    @PutMapping("/{fineId}")
    public ResponseEntity<BorrowFineDTO> updateFine(@PathVariable Long fineId, @RequestBody UpdateFineRequest request) {
        BorrowFineDTO fine = borrowService.updateFine(fineId, request.getAmount(), request.getReason());
        return ResponseEntity.ok(fine);
    }

    @PutMapping("/{fineId}/pay")
    public ResponseEntity<BorrowFineDTO> payFine(@PathVariable Long fineId) {
        BorrowFineDTO updatedFine = borrowService.payFine(fineId);
        return ResponseEntity.ok(updatedFine);
    }

    @DeleteMapping("/{fineId}")
    public ResponseEntity<Void> deleteFine(@PathVariable Long fineId) {
        borrowService.deleteFine(fineId);
        return ResponseEntity.noContent().build();
    }

    public static class CreateFineRequest {
        private Long borrowId;
        private Long userId;
        private BigDecimal amount;
        private BorrowFine.FineReason reason;

        // getters and setters
        public Long getBorrowId() { return borrowId; }
        public void setBorrowId(Long borrowId) { this.borrowId = borrowId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BorrowFine.FineReason getReason() { return reason; }
        public void setReason(BorrowFine.FineReason reason) { this.reason = reason; }
    }

    public static class UpdateFineRequest {
        private BigDecimal amount;
        private BorrowFine.FineReason reason;

        // getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BorrowFine.FineReason getReason() { return reason; }
        public void setReason(BorrowFine.FineReason reason) { this.reason = reason; }
    }
}