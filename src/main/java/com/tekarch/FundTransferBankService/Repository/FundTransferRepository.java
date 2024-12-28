package com.tekarch.FundTransferBankService.Repository;
import com.tekarch.FundTransferBankService.Model.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {

    @Query("SELECT ft FROM FundTransfer ft WHERE (ft.senderAccountId = :userId OR ft.receiverAccountId = :userId) " +
            "AND (:type IS NULL OR ft.status = :type) " +
            "AND (:minAmount IS NULL OR ft.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR ft.amount <= :maxAmount)")
    List<FundTransfer> findAllByUserIdAndFilters(@Param("userId") Long userId,
                                                 @Param("type") String type,
                                                 @Param("minAmount") BigDecimal minAmount,
                                                 @Param("maxAmount") BigDecimal maxAmount);

    List<FundTransfer> findAllBySenderAccountIdOrReceiverAccountId(Long senderAccountId, Long receiverAccountId);

    @Query("SELECT COALESCE(SUM(ft.amount), 0) FROM FundTransfer ft " +
            "WHERE ft.senderAccountId = :accountId AND DATE(ft.initiatedAt) = :currentDate")
    BigDecimal findUsedAmountToday(@Param("accountId") Long accountId, @Param("currentDate") LocalDate currentDate);
}
