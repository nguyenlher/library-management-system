package com.library.borrow_service.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.borrow_service.entity.Borrow;
import com.library.borrow_service.entity.BorrowFine;
import com.library.borrow_service.repository.BorrowFineRepository;
import com.library.borrow_service.repository.BorrowRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BorrowRepository borrowRepository;
    private final BorrowFineRepository borrowFineRepository;

    public DataInitializer(BorrowRepository borrowRepository, BorrowFineRepository borrowFineRepository) {
        this.borrowRepository = borrowRepository;
        this.borrowFineRepository = borrowFineRepository;
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
        borrow3.setDueDate(now.minusDays(11));
        borrow3.setReturnDate(now.minusDays(8));
        borrow3.setStatus(Borrow.BorrowStatus.LATE_RETURNED);
        borrowRepository.save(borrow3);

        Borrow borrow4 = new Borrow();
        borrow4.setUserId(4L);
        borrow4.setBookId(4L);
        borrow4.setBorrowDate(now.minusDays(15));
        borrow4.setDueDate(now.minusDays(1));
        borrow4.setReturnDate(now.minusDays(1));
        borrow4.setStatus(Borrow.BorrowStatus.LOST);
        borrowRepository.save(borrow4);


        Borrow borrow5 = new Borrow();
        borrow5.setUserId(5L);
        borrow5.setBookId(5L);
        borrow5.setBorrowDate(now.minusDays(3));
        borrow5.setDueDate(now.plusDays(11));
        borrow5.setStatus(Borrow.BorrowStatus.BORROWED);
        borrowRepository.save(borrow5);

        BorrowFine lateFine = new BorrowFine();
        lateFine.setBorrowId(borrow3.getId());
        lateFine.setUserId(3L);
        lateFine.setAmount(BigDecimal.valueOf(2.00));
        lateFine.setReason(BorrowFine.FineReason.LATE);
        lateFine.setPaid(false);
        borrowFineRepository.save(lateFine);

        BorrowFine lostFine = new BorrowFine();
        lostFine.setBorrowId(borrow4.getId());
        lostFine.setUserId(4L);
        lostFine.setAmount(BigDecimal.valueOf(20.00));
        lostFine.setReason(BorrowFine.FineReason.LOST);
        lostFine.setPaid(false);
        borrowFineRepository.save(lostFine);

        BorrowFine paidFine = new BorrowFine();
        paidFine.setBorrowId(borrow2.getId());
        paidFine.setUserId(4L);
        paidFine.setAmount(BigDecimal.valueOf(1.50));
        paidFine.setReason(BorrowFine.FineReason.DAMAGE);
        paidFine.setPaid(true);
        borrowFineRepository.save(paidFine);
    }
}