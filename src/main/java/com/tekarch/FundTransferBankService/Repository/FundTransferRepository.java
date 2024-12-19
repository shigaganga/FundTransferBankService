package com.tekarch.FundTransferBankService.Repository;

import com.tekarch.FundTransferBankService.Model.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    // Find transactions by senderAccountId or receiverAccountId
    List<FundTransfer> findBySenderAccountIdOrReceiverAccountId(Long senderAccountId, Long receiverAccountId);

    // Find transactions for an account after a specific time
    List<FundTransfer> findBySenderAccountIdAndInitiatedAtAfter(Long senderAccountId, LocalDateTime initiatedAfter);
}
