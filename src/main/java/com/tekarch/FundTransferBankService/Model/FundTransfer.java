package com.tekarch.FundTransferBankService.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL equivalent for auto-increment
    private Long transferId;

    @Column(name = "sender_account_id", nullable = false)
    private Long senderAccountId; // Replace Account entity with sender account ID

    @Column(name = "receiver_account_id", nullable = false)
    private Long receiverAccountId; // Replace Account entity with receiver account ID

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status = "pending"; // Default value

    @Column(nullable = false, updatable = false)
    private LocalDateTime initiatedAt = LocalDateTime.now(); // Default value - CURRENT_TIMESTAMP
    private LocalDate scheduledAt; // Add this field for scheduling transfers
    private LocalDateTime completedAt; // Nullable, will be populated when transfer is completed
    private LocalDateTime cancelledAt;  // Add this field for cancellations
   private String frequency; // Optional, for recurring transfers
}
