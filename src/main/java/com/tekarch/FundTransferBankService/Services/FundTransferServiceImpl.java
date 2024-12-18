package com.tekarch.FundTransferBankService.Services;

import com.tekarch.FundTransferBankService.Model.FundTransfer;
import com.tekarch.FundTransferBankService.Repository.FundTransferRepository;
import com.tekarch.FundTransferBankService.Services.Interfaces.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
    @Service
    public class FundTransferServiceImpl implements FundTransferService {

        @Autowired
        private FundTransferRepository fundTransferRepository;

        //@Autowired
        //private AccountService accountService; // Inject the AccountService for user account operations

        // Implementing initiate fund transfer
        @Override
        public FundTransfer initiateTransfer(FundTransfer transfer) {
            // Logic for intra-bank transfer or inter-bank transfer (optional integration with external systems)
            // Process the fund transfer and return the saved transfer object
            return fundTransferRepository.save(transfer);
        }

        // Implementing get all transactions for a user
        @Override
        public List<FundTransfer> getTransactionsByUser(Long userId) {
            return fundTransferRepository.findBySenderAccount_UserIdOrReceiverAccount_UserId(userId, userId);
        }

        // Implementing get all transactions for an account
        @Override
        public List<FundTransfer> getTransactionsByAccount(Long accountId) {
            return fundTransferRepository.findBySenderAccount_AccountIdOrReceiverAccount_AccountId(accountId, accountId);
        }

        // Implementing get a specific transaction by its ID
        @Override
        public FundTransfer getTransactionById(Long transactionId) {
            return fundTransferRepository.findById(transactionId)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        }

        // Implementing validate the transaction limit
        @Override
        public boolean validateTransactionLimit(Long accountId) {
            // Fetch the account and validate the daily transaction limit
            // For simplicity, assume we return a boolean (true if under limit)
            return true;
        }

        // Implementing schedule an autopay (optional)
        @Override
        public void scheduleAutopay(FundTransfer transfer) {
            // Logic to schedule the autopay, store in the database, or schedule a task
        }

        // Implementing update an autopay
        @Override
        public void updateAutopay(Long autopayId, FundTransfer transfer) {
            // Logic to update an existing autopay
        }

        // Implementing cancel an autopay
        @Override
        public void cancelAutopay(Long autopayId) {
            // Logic to cancel a scheduled autopay
        }
    }

