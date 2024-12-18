package com.tekarch.FundTransferBankService.Services.Interfaces;

import com.tekarch.FundTransferBankService.Model.FundTransfer;

import java.util.List;

public interface FundTransferService {
    // Initiate fund transfer
    FundTransfer initiateTransfer(FundTransfer transfer);

    // Get all transactions for a user
    List<FundTransfer> getTransactionsByUser(Long userId);

    // Get all transactions for an account
    List<FundTransfer> getTransactionsByAccount(Long accountId);

    // Get a specific transaction by its ID
    FundTransfer getTransactionById(Long transactionId);

    // Validate the transaction limit
    boolean validateTransactionLimit(Long accountId);

    // Schedule an autopay (optional)
    void scheduleAutopay(FundTransfer transfer);

    // Update an autopay
    void updateAutopay(Long autopayId, FundTransfer transfer);

    // Cancel an autopay
    void cancelAutopay(Long autopayId);
}
