package com.tekarch.FundTransferBankService.Repository;

import com.tekarch.FundTransferBankService.Model.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long>{
    // Custom query to find transactions by userId
    List<FundTransfer> findBySenderAccount_UserIdOrReceiverAccount_UserId(Long senderUserId, Long receiverUserId);

    // Custom query to find transactions by accountId
    List<FundTransfer> findBySenderAccount_AccountIdOrReceiverAccount_AccountId(Long senderAccountId, Long receiverAccountId);
}
