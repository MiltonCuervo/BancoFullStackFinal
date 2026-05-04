package com.udea.lab12025p.controller;

import com.udea.lab12025p.dto.TransactionDTO;
import com.udea.lab12025p.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/transactions", produces = "application/json")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    // Constructor Injection Replaced Field Injection
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> transferMoney(@RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO savedTransaction = transactionService.transferMoney(transactionDTO);
            return ResponseEntity.ok(savedTransaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{accountNumber}")
    public List<TransactionDTO> getTransactionsByAccount(@PathVariable String accountNumber) {
        return transactionService.getTransactionsForAccount(accountNumber);
    }
}
