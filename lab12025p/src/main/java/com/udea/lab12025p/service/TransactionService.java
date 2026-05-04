package com.udea.lab12025p.service;

import com.udea.lab12025p.dto.TransactionDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.entity.Transaction;
import com.udea.lab12025p.repository.CustomerRepository;
import com.udea.lab12025p.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Sustituye el constructor manual (Inyección por constructor limpia)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        validateTransactionRequest(transactionDTO);

        Customer sender = customerRepository.findByAccountNumberForUpdate(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del remitente no existe"));
        
        Customer receiver = customerRepository.findByAccountNumberForUpdate(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del receptor no existe"));

        executeMoneyTransfer(sender, receiver, transactionDTO);

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .senderAccountNumber(sender.getAccountNumber())
                .receiverAccountNumber(receiver.getAccountNumber())
                .amount(transactionDTO.getAmount())
                .build());

        return convertToDTO(transaction);
    }

    private void validateTransactionRequest(TransactionDTO dto) {
        if (dto.getSenderAccountNumber() == null || dto.getReceiverAccountNumber() == null) {
            throw new IllegalArgumentException("Los numeros de cuenta son obligatorios");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (dto.getSenderAccountNumber().equals(dto.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("No puede transferir dinero a su propia cuenta");
        }
    }

    private void executeMoneyTransfer(Customer sender, Customer receiver, TransactionDTO dto) {
        if (sender.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("El remitente no tiene saldo suficiente");
        }
        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        
        customerRepository.save(sender);
        customerRepository.save(receiver);
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        return transactionRepository
                .findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private TransactionDTO convertToDTO(Transaction tx) {
        return TransactionDTO.builder()
                .id(tx.getId())
                .senderAccountNumber(tx.getSenderAccountNumber())
                .receiverAccountNumber(tx.getReceiverAccountNumber())
                .amount(tx.getAmount())
                .transactionDate(tx.getTransactionDate())
                .build();
    }
}