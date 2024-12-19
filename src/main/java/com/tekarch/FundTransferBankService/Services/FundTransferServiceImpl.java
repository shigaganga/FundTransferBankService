package com.tekarch.FundTransferBankService.Services;

import com.tekarch.FundTransferBankService.Model.FundTransfer;
import com.tekarch.FundTransferBankService.Repository.FundTransferRepository;
import com.tekarch.FundTransferBankService.Services.Interfaces.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    @Autowired
    private FundTransferRepository fundTransferRepository;

    @Override
    public FundTransfer initiateTransfer(FundTransfer transfer) {
        // Perform validations
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        // Set initial status and timestamps
        transfer.setStatus("pending");
        transfer.setInitiatedAt(LocalDateTime.now());

        // Save the transfer object
        return fundTransferRepository.save(transfer);
    }

    @Override

    public List<FundTransfer> getTransactionsByUser(Long userId) {
        // Assuming `FundTransferRepository` has a method to fetch transactions by user ID
        return fundTransferRepository.findBySenderAccountIdOrReceiverAccountId(userId, userId);
    }
/*
    @Override
    public List<FundTransfer> getTransactionsByAccount(Long accountId) {
        // Assuming `FundTransferRepository` has a method to fetch transactions by account ID
        return fundTransferRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);
    }

    @Override
    public FundTransfer getTransactionById(Long transactionId) {
        // Fetch and return the transaction by its ID
        return fundTransferRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
    }

    @Override
    public boolean validateTransactionLimit(Long accountId, BigDecimal amount) {
        // Assuming a predefined transaction limit, e.g., $10,000
        BigDecimal transactionLimit = new BigDecimal("10000");

        // Fetch today's transactions for the account
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<FundTransfer> transactions = fundTransferRepository.findBySenderAccountIdAndInitiatedAtAfter(accountId, startOfDay);

        // Calculate total transferred amount for the day
        BigDecimal totalTransferred = transactions.stream()
                .map(FundTransfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Check if the new transfer exceeds the limit
        return totalTransferred.add(amount).compareTo(transactionLimit) <= 0;
    }

    @Override*/
@Override
public FundTransfer scheduleAutopay(FundTransfer transfer) {
    transfer.setStatus("scheduled");
    transfer.setInitiatedAt(LocalDateTime.now());
    return fundTransferRepository.save(transfer);
}


    @Override
    public void updateAutopay(Long autopayId, FundTransfer transfer) {
        // Fetch the existing autopay record
        FundTransfer existingAutopay = fundTransferRepository.findById(autopayId)
                .orElseThrow(() -> new IllegalArgumentException("Autopay not found with ID: " + autopayId));

        // Update necessary fields and save
        existingAutopay.setAmount(transfer.getAmount());
        existingAutopay.setReceiverAccountId(transfer.getReceiverAccountId());
        fundTransferRepository.save(existingAutopay);
    }

    @Override
    public void cancelAutopay(Long autopayId) {
        // Fetch the autopay record
        FundTransfer existingAutopay = fundTransferRepository.findById(autopayId)
                .orElseThrow(() -> new IllegalArgumentException("Autopay not found with ID: " + autopayId));

        // Update status to "cancelled" and save
        existingAutopay.setStatus("cancelled");
        fundTransferRepository.save(existingAutopay);
    }
}
