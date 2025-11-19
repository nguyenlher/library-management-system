package com.library.borrow_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.borrow_service.dto.BorrowDTO;
import com.library.borrow_service.dto.BorrowFineDTO;
import com.library.borrow_service.entity.Borrow;
import com.library.borrow_service.entity.BorrowFine;
import com.library.borrow_service.repository.BorrowFineRepository;
import com.library.borrow_service.repository.BorrowRepository;

@Service
@Transactional
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BorrowFineRepository borrowFineRepository;

    // Fine rates
    private static final BigDecimal LATE_FINE_RATE = BigDecimal.valueOf(0.50); // $0.50 per day
    private static final int BORROW_PERIOD_DAYS = 14; // 2 weeks

    public BorrowService(BorrowRepository borrowRepository, BorrowFineRepository borrowFineRepository) {
        this.borrowRepository = borrowRepository;
        this.borrowFineRepository = borrowFineRepository;
    }

    // Borrow operations
    public List<BorrowDTO> getAllBorrows() {
        return borrowRepository.findAll().stream()
                .map(this::convertBorrowToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BorrowDTO> getBorrowById(Long id) {
        return borrowRepository.findById(id)
                .map(this::convertBorrowToDTO);
    }

    public List<BorrowDTO> getBorrowsByUser(Long userId) {
        return borrowRepository.findByUserId(userId).stream()
                .map(this::convertBorrowToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowDTO> getBorrowsByBook(Long bookId) {
        return borrowRepository.findByBookId(bookId).stream()
                .map(this::convertBorrowToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowDTO> getActiveBorrowsByUser(Long userId) {
        return borrowRepository.findActiveBorrowsByUser(userId, Borrow.BorrowStatus.BORROWED).stream()
                .map(this::convertBorrowToDTO)
                .collect(Collectors.toList());
    }

    public Long countActiveBorrowsByUser(Long userId) {
        return borrowRepository.countActiveBorrowsByUser(userId, Borrow.BorrowStatus.BORROWED);
    }

    public BorrowDTO borrowBook(Long userId, Long bookId) {
        // Check if user already has this book borrowed
        List<Borrow> activeBorrows = borrowRepository.findActiveBorrowsByUser(userId, Borrow.BorrowStatus.BORROWED);
        boolean alreadyBorrowed = activeBorrows.stream()
                .anyMatch(borrow -> borrow.getBookId().equals(bookId));

        if (alreadyBorrowed) {
            throw new IllegalStateException("User already has this book borrowed");
        }

        // Check borrow limit (max 5 books per user)
        if (countActiveBorrowsByUser(userId) >= 5) {
            throw new IllegalStateException("User has reached maximum borrow limit");
        }

        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(BORROW_PERIOD_DAYS));
        borrow.setStatus(Borrow.BorrowStatus.BORROWED);

        Borrow savedBorrow = borrowRepository.save(borrow);
        return convertBorrowToDTO(savedBorrow);
    }

    public BorrowDTO returnBook(Long borrowId) {
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);
        if (borrowOpt.isEmpty()) {
            throw new IllegalArgumentException("Borrow record not found");
        }

        Borrow borrow = borrowOpt.get();
        if (borrow.getStatus() != Borrow.BorrowStatus.BORROWED) {
            throw new IllegalStateException("Book is not currently borrowed");
        }

        LocalDateTime returnDate = LocalDateTime.now();
        borrow.setReturnDate(returnDate);

        // Check if late return
        if (returnDate.isAfter(borrow.getDueDate())) {
            borrow.setStatus(Borrow.BorrowStatus.LATE_RETURNED);
            // Calculate and create late fine
            createLateFine(borrow, returnDate);
        } else {
            borrow.setStatus(Borrow.BorrowStatus.RETURNED);
        }

        Borrow savedBorrow = borrowRepository.save(borrow);
        return convertBorrowToDTO(savedBorrow);
    }

    public BorrowDTO reportLostBook(Long borrowId) {
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);
        if (borrowOpt.isEmpty()) {
            throw new IllegalArgumentException("Borrow record not found");
        }

        Borrow borrow = borrowOpt.get();
        if (borrow.getStatus() != Borrow.BorrowStatus.BORROWED) {
            throw new IllegalStateException("Book is not currently borrowed");
        }

        borrow.setStatus(Borrow.BorrowStatus.LOST);
        borrow.setReturnDate(LocalDateTime.now());

        // Create lost fine (assume book value is $20)
        BorrowFine lostFine = new BorrowFine(borrow.getId(), borrow.getUserId(),
                                           BigDecimal.valueOf(20.00), BorrowFine.FineReason.LOST);
        borrowFineRepository.save(lostFine);

        Borrow savedBorrow = borrowRepository.save(borrow);
        return convertBorrowToDTO(savedBorrow);
    }

    // Fine operations
    public List<BorrowFineDTO> getAllFines() {
        return borrowFineRepository.findAll().stream()
                .map(this::convertFineToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowFineDTO> getFinesByUser(Long userId) {
        return borrowFineRepository.findByUserId(userId).stream()
                .map(this::convertFineToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowFineDTO> getUnpaidFinesByUser(Long userId) {
        return borrowFineRepository.findByUserIdAndPaid(userId, false).stream()
                .map(this::convertFineToDTO)
                .collect(Collectors.toList());
    }

    public BorrowFineDTO payFine(Long fineId) {
        Optional<BorrowFine> fineOpt = borrowFineRepository.findById(fineId);
        if (fineOpt.isEmpty()) {
            throw new IllegalArgumentException("Fine record not found");
        }

        BorrowFine fine = fineOpt.get();
        if (fine.getPaid()) {
            throw new IllegalStateException("Fine is already paid");
        }

        fine.setPaid(true);
        BorrowFine savedFine = borrowFineRepository.save(fine);
        return convertFineToDTO(savedFine);
    }

    public BigDecimal getTotalUnpaidFinesByUser(Long userId) {
        return borrowFineRepository.findByUserIdAndPaid(userId, false).stream()
                .map(BorrowFine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper methods
    private void createLateFine(Borrow borrow, LocalDateTime returnDate) {
        long daysLate = java.time.Duration.between(borrow.getDueDate(), returnDate).toDays();
        if (daysLate > 0) {
            BigDecimal fineAmount = LATE_FINE_RATE.multiply(BigDecimal.valueOf(daysLate));
            BorrowFine lateFine = new BorrowFine(borrow.getId(), borrow.getUserId(),
                                               fineAmount, BorrowFine.FineReason.LATE);
            borrowFineRepository.save(lateFine);
        }
    }

    private BorrowDTO convertBorrowToDTO(Borrow borrow) {
        BorrowDTO dto = new BorrowDTO();
        dto.setId(borrow.getId());
        dto.setUserId(borrow.getUserId());
        dto.setBookId(borrow.getBookId());
        dto.setBorrowDate(borrow.getBorrowDate());
        dto.setDueDate(borrow.getDueDate());
        dto.setReturnDate(borrow.getReturnDate());
        dto.setStatus(borrow.getStatus());
        dto.setCreatedAt(borrow.getCreatedAt());
        dto.setUpdatedAt(borrow.getUpdatedAt());
        return dto;
    }

    private BorrowFineDTO convertFineToDTO(BorrowFine fine) {
        BorrowFineDTO dto = new BorrowFineDTO();
        dto.setId(fine.getId());
        dto.setBorrowId(fine.getBorrowId());
        dto.setUserId(fine.getUserId());
        dto.setAmount(fine.getAmount());
        dto.setReason(fine.getReason());
        dto.setPaid(fine.getPaid());
        dto.setCreatedAt(fine.getCreatedAt());
        return dto;
    }
}