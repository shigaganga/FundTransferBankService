package com.tekarch.FundTransferBankService.Controller;

import com.tekarch.FundTransferBankService.DTO.AccountDTO;
import com.tekarch.FundTransferBankService.DTO.AutopayRequest;
import com.tekarch.FundTransferBankService.DTO.TransferRequest;
import com.tekarch.FundTransferBankService.Model.FundTransfer;
import com.tekarch.FundTransferBankService.Services.FundTransferServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fund-transfer")
@RequiredArgsConstructor
public class FundTransferController {

    private final FundTransferServiceImpl fundTransferService;

    // Check if account exists
    @GetMapping("/accounts/{accountId}/exists")
    public ResponseEntity<Boolean> isAccountExists(@PathVariable Long accountId) {
        boolean exists = fundTransferService.isAccountExists(accountId);
        return ResponseEntity.ok(exists);
    }

    // Initiate transfer
    @PostMapping("/transfer")
    public ResponseEntity<FundTransfer> initiateTransfer(@RequestParam Long senderId,
                                                         @RequestParam Long receiverId,
                                                         @RequestParam BigDecimal amount) {
        FundTransfer transfer = fundTransferService.initiateTransfer(senderId, receiverId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }

    // Get user transactions with filters
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<FundTransfer>> getUserTransactions(@PathVariable Long userId,
                                                                  @RequestParam(required = false) String type,
                                                                  @RequestParam(required = false) BigDecimal minAmount,
                                                                  @RequestParam(required = false) BigDecimal maxAmount) {
        List<FundTransfer> transactions = fundTransferService.getUserTransactions(userId, type, minAmount, maxAmount);
        return ResponseEntity.ok(transactions);
    }

    // Get account transactions
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<FundTransfer>> getAccountTransactions(@PathVariable Long accountId) {
        List<FundTransfer> transactions = fundTransferService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Get transaction details by ID
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<FundTransfer> getTransactionDetails(@PathVariable Long transactionId) {
        FundTransfer transfer = fundTransferService.getTransactionDetails(transactionId);
        return ResponseEntity.ok(transfer);
    }

    // Get transaction limit for an account
    @GetMapping("/accounts/{accountId}/transaction-limit")
    public ResponseEntity<BigDecimal> getTransactionLimit(@PathVariable Long accountId) {
        BigDecimal limit = fundTransferService.getTransactionLimit(accountId);
        if (limit != null) {
            return ResponseEntity.ok(limit);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Schedule a transfer with query
    @PostMapping("/schedule-query")
    public ResponseEntity<FundTransfer> scheduleTransfer(@RequestParam Long senderId,
                                                         @RequestParam Long receiverId,
                                                         @RequestParam BigDecimal amount,
                                                         @RequestParam LocalDate scheduleDate) {
        FundTransfer transfer = fundTransferService.scheduleTransfer(senderId, receiverId, amount, scheduleDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }
    @PostMapping("/schedule")
    public ResponseEntity<FundTransfer> scheduleTransfer(@RequestBody TransferRequest transferRequest) {
        FundTransfer transfer = fundTransferService.scheduleTransfer(
                transferRequest.getSenderId(),
                transferRequest.getReceiverId(),
                transferRequest.getAmount(),
                transferRequest.getScheduleDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }


    // Setup a recurring transfer
    @PostMapping("/recurring")
    public ResponseEntity<FundTransfer> setupRecurringTransfer(@RequestParam Long senderId,
                                                               @RequestParam Long receiverId,
                                                               @RequestParam BigDecimal amount,
                                                               @RequestParam String frequency) {
        FundTransfer transfer = fundTransferService.setupRecurringTransfer(senderId, receiverId, amount, frequency);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }

    // Get scheduled or recurring transfer details
    @GetMapping("/transfer/{transferId}")
    public ResponseEntity<FundTransfer> getScheduledOrRecurringTransfer(@PathVariable Long transferId) {
        FundTransfer transfer = fundTransferService.getScheduledOrRecurringTransfer(transferId);
        return ResponseEntity.ok(transfer);
    }

    // Cancel a transfer
    @DeleteMapping("/transfer/{transferId}/cancel")
    public ResponseEntity<Void> cancelTransfer(@PathVariable Long transferId) {
        fundTransferService.cancelTransfer(transferId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/autopay")
    public ResponseEntity<FundTransfer> scheduleAutopay(@RequestBody AutopayRequest autopayRequest) {
        try {
            // Extract data from AutopayRequest DTO
            Long senderId = autopayRequest.getSenderId();
            Long receiverId = autopayRequest.getReceiverId();
            BigDecimal amount = autopayRequest.getAmount();
            LocalDate scheduleDate = autopayRequest.getScheduleDate();

            // Schedule the autopay transfer using the service method
            FundTransfer autopay = fundTransferService.scheduleAutopay(senderId, receiverId, amount, scheduleDate);

            if (autopay != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(autopay);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            // Return an error response if something goes wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update autopay transfer details
    @PutMapping("/autopay/{autopayId}")
    public ResponseEntity<FundTransfer> updateAutopay(@PathVariable Long autopayId,
                                                      @RequestParam(required = false) Long senderId,
                                                      @RequestParam(required = false) Long receiverId,
                                                      @RequestParam(required = false) BigDecimal amount,
                                                      @RequestParam(required = false) LocalDate newScheduleDate) {
        FundTransfer autopay = fundTransferService.updateAutopay(autopayId, senderId, receiverId, amount, newScheduleDate);
        return ResponseEntity.ok(autopay);
    }

    // Cancel autopay transfer
    @DeleteMapping("/autopay/{autopayId}/cancel")
    public ResponseEntity<Void> cancelAutopay(@PathVariable Long autopayId) {
        fundTransferService.cancelAutopay(autopayId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
