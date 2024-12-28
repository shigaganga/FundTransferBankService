package com.tekarch.FundTransferBankService.Services;

import com.tekarch.FundTransferBankService.DTO.AccountDTO;
import com.tekarch.FundTransferBankService.Model.FundTransfer;
import com.tekarch.FundTransferBankService.Repository.FundTransferRepository;
import com.tekarch.FundTransferBankService.Services.Interfaces.FundTransferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FundTransferServiceImpl implements FundTransferService {

    private static final Logger logger = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    @Value("${account.ms.url:http://localhost:8081/accounts}")
    private String accountMsUrl;

    private final RestTemplate restTemplate;
    private final FundTransferRepository fundTransferRepository;
    public boolean isAccountExists(Long accountId) {
        String url = accountMsUrl + "/" + accountId;
        try {
            logger.info("Validating account existence for Account ID: {}", accountId);
            ResponseEntity<AccountDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    AccountDTO.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error occurred while validating account existence for Account ID {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public FundTransfer initiateTransfer(Long senderId, Long receiverId, BigDecimal amount) {
        // Validate accounts
        if (!isAccountExists(senderId) || !isAccountExists(receiverId)) {
            throw new RuntimeException("Either sender or receiver account does not exist");
        }

        FundTransfer transfer = new FundTransfer();
        transfer.setSenderAccountId(senderId);
        transfer.setReceiverAccountId(receiverId);
        transfer.setAmount(amount);
        transfer.setStatus("pending");
        transfer.setInitiatedAt(LocalDateTime.now());

        return fundTransferRepository.save(transfer);
    }

    @Override
    public List<FundTransfer> getUserTransactions(Long userId, String type, BigDecimal minAmount, BigDecimal maxAmount) {
        return fundTransferRepository.findAllByUserIdAndFilters(userId, type, minAmount, maxAmount);
    }

    @Override
    public List<FundTransfer> getAccountTransactions(Long accountId) {
        return fundTransferRepository.findAllBySenderAccountIdOrReceiverAccountId(accountId, accountId);
    }

    @Override
    public FundTransfer getTransactionDetails(Long transactionId) {
        return fundTransferRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public BigDecimal getTransactionLimit(Long accountId) {
        String url = accountMsUrl + "/" + accountId + "/transactionlimit";
        try {
            logger.info("Fetching transaction limit for Account ID: {}", accountId);

            ResponseEntity<BigDecimal> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    BigDecimal.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                BigDecimal transactionLimit = response.getBody();
                logger.info("Fetched transaction limit for Account ID {}: {}", accountId, transactionLimit);
                return transactionLimit;
            } else {
                logger.warn("Could not fetch transaction limit for Account ID: {}", accountId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while fetching transaction limit for Account ID {}: {}", accountId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FundTransfer scheduleTransfer(Long senderId, Long receiverId, BigDecimal amount, LocalDate scheduleDate) {
        if (!isAccountExists(senderId) || !isAccountExists(receiverId)) {
            throw new RuntimeException("Either sender or receiver account does not exist");
        }

        FundTransfer transfer = new FundTransfer();
        transfer.setSenderAccountId(senderId);
        transfer.setReceiverAccountId(receiverId);
        transfer.setAmount(amount);
        transfer.setStatus("scheduled");
        transfer.setScheduledAt(scheduleDate);
        transfer.setInitiatedAt(LocalDateTime.now());

        return fundTransferRepository.save(transfer);
    }

    @Override
    public FundTransfer setupRecurringTransfer(Long senderId, Long receiverId, BigDecimal amount, String frequency) {
        if (!isAccountExists(senderId) || !isAccountExists(receiverId)) {
            throw new RuntimeException("Either sender or receiver account does not exist");
        }

        if (!isValidFrequency(frequency)) {
            throw new IllegalArgumentException("Invalid frequency: " + frequency);
        }

        FundTransfer transfer = new FundTransfer();
        transfer.setSenderAccountId(senderId);
        transfer.setReceiverAccountId(receiverId);
        transfer.setAmount(amount);
        transfer.setFrequency(frequency);
        transfer.setStatus("recurring");
        transfer.setInitiatedAt(LocalDateTime.now());

        return fundTransferRepository.save(transfer);
    }

    @Override
    public FundTransfer getScheduledOrRecurringTransfer(Long transferId) {
        logger.info("Fetching scheduled or recurring transfer with ID: {}", transferId);
        return fundTransferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Scheduled or Recurring Transfer not found for ID: " + transferId));
    }

    @Override
    public void cancelTransfer(Long transferId) {
        logger.info("Attempting to cancel transfer with ID: {}", transferId);

        FundTransfer transfer = fundTransferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found for ID: " + transferId));

        if ("completed".equalsIgnoreCase(transfer.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed transfer");
        }

        if ("canceled".equalsIgnoreCase(transfer.getStatus())) {
            throw new IllegalStateException("Transfer is already canceled");
        }

        transfer.setStatus("canceled");
        transfer.setCancelledAt(LocalDateTime.now());

        fundTransferRepository.save(transfer);

        logger.info("Transfer with ID {} has been successfully canceled.", transferId);
    }

    @Override
    public FundTransfer scheduleAutopay(Long senderId, Long receiverId, BigDecimal amount, LocalDate scheduleDate) {
        if (!isAccountExists(senderId)) {
            throw new RuntimeException("Sender account does not exist.");
        }

        if (!isAccountExists(receiverId)) {
            throw new RuntimeException("Receiver account does not exist.");
        }

        if (scheduleDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Schedule date cannot be in the past.");
        }

        FundTransfer autopay = new FundTransfer();
        autopay.setSenderAccountId(senderId);
        autopay.setReceiverAccountId(receiverId);
        autopay.setAmount(amount);
        autopay.setStatus("scheduled");
        autopay.setInitiatedAt(LocalDateTime.now());
        autopay.setScheduledAt(scheduleDate);

        return fundTransferRepository.save(autopay);
    }

    @Override
    public FundTransfer updateAutopay(Long autopayId, Long senderId, Long receiverId, BigDecimal amount, LocalDate newScheduleDate) {
        FundTransfer autopay = fundTransferRepository.findById(autopayId)
                .orElseThrow(() -> new RuntimeException("Autopay with ID " + autopayId + " not found"));

        if (senderId != null) {
            autopay.setSenderAccountId(senderId);
        }

        if (receiverId != null) {
            autopay.setReceiverAccountId(receiverId);
        }

        if (amount != null) {
            autopay.setAmount(amount);
        }

        if (newScheduleDate != null) {
            autopay.setScheduledAt(newScheduleDate);
        }

        return fundTransferRepository.save(autopay);
    }

    @Override
    public void cancelAutopay(Long autopayId) {
        FundTransfer autopay = fundTransferRepository.findById(autopayId)
                .orElseThrow(() -> new RuntimeException("Autopay with ID " + autopayId + " not found"));

        autopay.setCancelledAt(LocalDateTime.now());
        autopay.setStatus("canceled");

        fundTransferRepository.save(autopay);
    }

    private boolean isValidFrequency(String frequency) {
        return "daily".equalsIgnoreCase(frequency) ||
                "weekly".equalsIgnoreCase(frequency) ||
                "monthly".equalsIgnoreCase(frequency) ||
                "yearly".equalsIgnoreCase(frequency);
    }
}
