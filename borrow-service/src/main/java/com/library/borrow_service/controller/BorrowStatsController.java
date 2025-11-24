package com.library.borrow_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.borrow_service.service.BorrowService;

@RestController
public class BorrowStatsController {

    private final BorrowService borrowService;

    public BorrowStatsController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/borrows/count")
    public ResponseEntity<Map<String, Long>> countByBook(@RequestParam Long bookId) {
        Long count = borrowService.countBorrowsByBook(bookId);
        return ResponseEntity.ok(Map.of("bookId", bookId, "count", count));
    }
}
