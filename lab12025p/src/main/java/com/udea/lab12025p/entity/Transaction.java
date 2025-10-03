package com.udea.lab12025p.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transtactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_account_number", nullable = false)
    private String senderAccountNumber;
    @Column(name = "receiver_account_number", nullable = false)
    private String receiverAccountNumber;
    @Column(nullable = false)
    private Double amount;
    //@Column(nullable = false)
    //private LocalDateTime transactionDate;
}


