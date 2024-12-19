package com.tekarch.FundTransferBankService.Controller;
import com.tekarch.FundTransferBankService.Model.FundTransfer;
import com.tekarch.FundTransferBankService.Services.Interfaces.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fund-transfer")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    // Initiate a fund transfer
    @PostMapping
    public ResponseEntity<FundTransfer> initiateTransfer(@RequestBody FundTransfer transfer) {
        FundTransfer createdTransfer = fundTransferService.initiateTransfer(transfer);
        return ResponseEntity.ok(createdTransfer);
    }

  /*  // Get all transactions for a user (by userId)
    @GetMapping("/transaction/{userId}")
    public ResponseEntity<List<FundTransfer>> getTransactionsByUser(@PathVariable Long userId) {
        List<FundTransfer> transactions = fundTransferService.getTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }

    // Get all transactions for an account (by accountId)
    @GetMapping("/transaction/{accountId}")
    public ResponseEntity<List<FundTransfer>> getTransactionsByAccount(@PathVariable Long accountId) {
        List<FundTransfer> transactions = fundTransferService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Get a specific transaction by its ID
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<FundTransfer> getTransactionById(@PathVariable Long transactionId) {
        FundTransfer transaction = fundTransferService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    // Validate the transaction limit
    @GetMapping("/transaction-limit/{accountId}/validate")
    public ResponseEntity<Boolean> validateTransactionLimit(@PathVariable Long accountId) {
        boolean isValid = fundTransferService.validateTransactionLimit(accountId);
        return ResponseEntity.ok(isValid);
    }*/

    @PostMapping("/schedule-autopay")
    public ResponseEntity<FundTransfer> scheduleAutopay(@RequestBody FundTransfer fundTransfer) {
        FundTransfer savedAutopay = fundTransferService.scheduleAutopay(fundTransfer);
        return ResponseEntity.ok(savedAutopay);
    }


    // Optional: Update existing autopay
    @PutMapping("/scheduled-autopay/{autopayId}")
    public ResponseEntity<String> updateAutopay(@PathVariable Long autopayId, @RequestBody FundTransfer transfer) {
        fundTransferService.updateAutopay(autopayId, transfer);
        return ResponseEntity.ok("Autopay updated successfully.");
    }

    // Optional: Cancel scheduled autopay
    @DeleteMapping("/scheduled-autopay/{autopayId}")
    public ResponseEntity<String> cancelAutopay(@PathVariable Long autopayId) {
        fundTransferService.cancelAutopay(autopayId);
        return ResponseEntity.ok("Autopay canceled successfully.");
    }
}
