package com.library.borrow_service.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.borrow_service.entity.Borrow;
import com.library.borrow_service.repository.BorrowRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BorrowRepository borrowRepository;

    public DataInitializer(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (borrowRepository.count() == 0) {
            createSampleData();
            System.out.println("Sample borrow data initialized successfully!");
        } else {
            System.out.println("Borrow data already exists, skipping initialization.");
        }
    }

    private void createSampleData() {
        LocalDateTime now = LocalDateTime.now();

        Borrow borrow1 = new Borrow();
        borrow1.setUserId(3L);
        borrow1.setBookId(1L);
        borrow1.setBorrowDate(now.minusDays(5));
        borrow1.setDueDate(now.plusDays(9));
        borrow1.setStatus(Borrow.BorrowStatus.BORROWED);
        borrowRepository.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUserId(4L);
        borrow2.setBookId(2L);
        borrow2.setBorrowDate(now.minusDays(20));
        borrow2.setDueDate(now.minusDays(6));
        borrow2.setReturnDate(now.minusDays(8));
        borrow2.setStatus(Borrow.BorrowStatus.RETURNED);
        borrowRepository.save(borrow2);

        Borrow borrow3 = new Borrow();
        borrow3.setUserId(3L);
        borrow3.setBookId(3L);
        borrow3.setBorrowDate(now.minusDays(25));
        borrow3.setDueDate(now.minusDays(11)); // Due date was 11 days ago
        borrow3.setReturnDate(now.minusDays(8)); // Returned 8 days ago (3 days late)
        borrow3.setStatus(Borrow.BorrowStatus.RETURNED);
        borrowRepository.save(borrow3);

        Borrow borrow4 = new Borrow();
        borrow4.setUserId(4L);
        borrow4.setBookId(4L);  
        borrow4.setBorrowDate(now.minusDays(15));
        borrow4.setDueDate(now.minusDays(1)); // Due date was yesterday
        borrow4.setReturnDate(now.minusDays(1)); // Returned yesterday (on time)
        borrow4.setStatus(Borrow.BorrowStatus.RETURNED);
        borrowRepository.save(borrow4);


        Borrow borrow5 = new Borrow();
        borrow5.setUserId(5L);
        borrow5.setBookId(5L);
        borrow5.setBorrowDate(now.minusDays(3));
        borrow5.setDueDate(now.plusDays(11));
        borrow5.setStatus(Borrow.BorrowStatus.BORROWED);
        borrowRepository.save(borrow5);

        // Note: Fines are now calculated automatically based on return date vs due date
        // Additional fines (lost, damage) can be added manually if needed
    }
}