package com.library.borrow_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.library.borrow_service.service.BorrowService;

@Component
public class BorrowScheduler {

    private final BorrowService borrowService;

    public BorrowScheduler(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // Run every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void checkAndLockOverdueUsers() {
        borrowService.checkAndLockOverdueUsers();
    }
}