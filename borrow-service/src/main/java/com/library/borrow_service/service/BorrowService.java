package com.library.borrow_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    // Fine rates
    private static final BigDecimal LATE_FINE_RATE = BigDecimal.valueOf(10000); // 10,000 VND per day
    private static final int BORROW_PERIOD_DAYS = 14; // 2 weeks

    public BorrowService(BorrowRepository borrowRepository, BorrowFineRepository borrowFineRepository, RestTemplate restTemplate) {
        this.borrowRepository = borrowRepository;
        this.borrowFineRepository = borrowFineRepository;
        this.restTemplate = restTemplate;
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

    public Long countBorrowsByBook(Long bookId) {
        return borrowRepository.countByBookId(bookId);
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

    public BorrowFineDTO getFineById(Long fineId) {
        Optional<BorrowFine> fineOpt = borrowFineRepository.findById(fineId);
        return fineOpt.map(this::convertFineToDTO).orElse(null);
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

        // Check if user has no more unpaid fines, then unlock borrowing
        BigDecimal totalUnpaidFines = getTotalUnpaidFinesByUser(fine.getUserId());
        if (totalUnpaidFines.compareTo(BigDecimal.ZERO) == 0) {
            try {
                // Call user-service to unlock borrowing
                String url = "http://localhost:8081/users/" + fine.getUserId() + "/unlock";
                restTemplate.put(url, null);
            } catch (Exception e) {
                // Log error but don't fail the process
                System.err.println("Failed to unlock user " + fine.getUserId() + ": " + e.getMessage());
            }
        }

        return convertFineToDTO(savedFine);
    }

    public BorrowFineDTO createFine(Long borrowId, Long userId, BigDecimal amount, BorrowFine.FineReason reason) {
        BorrowFine fine = new BorrowFine(borrowId, userId, amount, reason);
        BorrowFine savedFine = borrowFineRepository.save(fine);
        return convertFineToDTO(savedFine);
    }

    public BorrowFineDTO updateFine(Long fineId, BigDecimal amount, BorrowFine.FineReason reason) {
        Optional<BorrowFine> fineOpt = borrowFineRepository.findById(fineId);
        if (fineOpt.isEmpty()) {
            throw new IllegalArgumentException("Fine record not found");
        }

        BorrowFine fine = fineOpt.get();
        fine.setAmount(amount);
        fine.setReason(reason);
        BorrowFine savedFine = borrowFineRepository.save(fine);
        return convertFineToDTO(savedFine);
    }

    public void deleteFine(Long fineId) {
        if (!borrowFineRepository.existsById(fineId)) {
            throw new IllegalArgumentException("Fine record not found");
        }
        borrowFineRepository.deleteById(fineId);
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

    private BigDecimal calculateFineAmount(Borrow borrow) {
        // For late returned books, calculate fine based on days late
        if (borrow.getStatus() == Borrow.BorrowStatus.LATE_RETURNED && borrow.getReturnDate() != null) {
            long daysLate = java.time.Duration.between(borrow.getDueDate(), borrow.getReturnDate()).toDays();
            if (daysLate > 0) {
                return LATE_FINE_RATE.multiply(BigDecimal.valueOf(daysLate));
            }
        }
        
        // For lost books, get fine from borrow_fines table
        if (borrow.getStatus() == Borrow.BorrowStatus.LOST) {
            List<BorrowFine> fines = borrowFineRepository.findByBorrowId(borrow.getId());
            return fines.stream()
                    .map(BorrowFine::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        return BigDecimal.ZERO;
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
        
        // Calculate fine amount
        dto.setFineAmount(calculateFineAmount(borrow));
        
        return dto;
    }

    private BorrowFineDTO convertFineToDTO(BorrowFine fine) {
        BorrowFineDTO dto = new BorrowFineDTO();
        dto.setId(fine.getId());
        dto.setBorrowId(fine.getBorrowId());
        dto.setUserId(fine.getUserId());
        // Get bookId from borrow
        Optional<Borrow> borrowOpt = borrowRepository.findById(fine.getBorrowId());
        if (borrowOpt.isPresent()) {
            dto.setBookId(borrowOpt.get().getBookId());
        }
        dto.setAmount(fine.getAmount());
        dto.setReason(fine.getReason());
        dto.setPaid(fine.getPaid());
        dto.setStatus(fine.getPaid() ? "PAID" : "UNPAID");
        dto.setCreatedAt(fine.getCreatedAt());
        return dto;
    }

    // Scheduled task to check and lock users with overdue borrows
    public void checkAndLockOverdueUsers() {
        LocalDateTime currentDate = LocalDateTime.now();

        // Find borrows that are overdue
        List<Borrow> overdueBorrows = borrowRepository.findOverdueBorrows(currentDate, Borrow.BorrowStatus.BORROWED);

        // Update status of overdue borrows to LATE_RETURNED
        for (Borrow borrow : overdueBorrows) {
            if (borrow.getDueDate().isBefore(currentDate)) {
                borrow.setStatus(Borrow.BorrowStatus.LATE_RETURNED);
                borrowRepository.save(borrow);
                System.out.println("Updated borrow " + borrow.getId() + " status to LATE_RETURNED due to overdue");
            }
        }

        // Group by userId to avoid multiple calls for same user
        overdueBorrows.stream()
                .collect(Collectors.groupingBy(Borrow::getUserId))
                .forEach((userId, borrows) -> {
                    // Check if any borrow is overdue
                    boolean hasOverdue = borrows.stream()
                            .anyMatch(borrow -> borrow.getDueDate().isBefore(currentDate));

                    if (hasOverdue) {
                        try {
                            // Call user-service to lock borrowing
                            String url = "http://localhost:8081/users/" + userId + "/lock";
                            restTemplate.put(url, null);
                        } catch (Exception e) {
                            // Log error but don't fail the process
                            System.err.println("Failed to lock user " + userId + ": " + e.getMessage());
                        }
                    }
                });
    }
}