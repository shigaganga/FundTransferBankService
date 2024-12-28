package com.tekarch.FundTransferBankService.Services.Interfaces;

import com.tekarch.FundTransferBankService.Model.FundTransfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FundTransferService {
    // Initiate and complete a transaction (Intra or Inter-bank)
    FundTransfer initiateTransfer(Long senderId, Long receiverId, BigDecimal amount);

    // Get all transactions for a user with optional filters
    List<FundTransfer> getUserTransactions(Long userId, String type, BigDecimal minAmount, BigDecimal maxAmount);

    // Get all transactions for a specific account
    List<FundTransfer> getAccountTransactions(Long accountId);

    // Get details of a specific transaction
    FundTransfer getTransactionDetails(Long transactionId);

    // Get transaction limits for an account
    BigDecimal getTransactionLimit(Long accountId);

    // Set up a scheduled transfer
    FundTransfer scheduleTransfer(Long senderId, Long receiverId, BigDecimal amount, LocalDate scheduleDate);

    // Set up a recurring transfer
    FundTransfer setupRecurringTransfer(Long senderId, Long receiverId, BigDecimal amount, String frequency);

    // Cancel a scheduled/recurring transfer
    void cancelTransfer(Long transferId);

    // Get details of a scheduled/recurring transfer
    FundTransfer getScheduledOrRecurringTransfer(Long transferId);

    // Set up an autopay (scheduled payment)
    FundTransfer scheduleAutopay(Long senderId, Long receiverId, BigDecimal amount, LocalDate scheduleDate);

    // Update an existing autopay (scheduled payment)
    FundTransfer updateAutopay(Long autopayId, Long senderId, Long receiverId, BigDecimal amount, LocalDate newScheduleDate);

    // Cancel an existing autopay
    void cancelAutopay(Long autopayId);


}
