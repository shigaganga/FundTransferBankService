package com.tekarch.FundTransferBankService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "sender_account_id", nullable = false) // Foreign Key to Account
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false) // Foreign Key to Account
    private Account receiverAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status = "pending"; // Default value

    @Column(nullable = false, updatable = false)
    private LocalDateTime initiatedAt = LocalDateTime.now(); // Default value - CURRENT_TIMESTAMP

    private LocalDateTime completedAt; // Nullable, will be populated when transfer is completed
}
