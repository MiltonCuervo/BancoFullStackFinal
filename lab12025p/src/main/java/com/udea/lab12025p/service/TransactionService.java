package com.udea.lab12025p.service;

import com.udea.lab12025p.dto.TransactionDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.entity.Transaction;
import com.udea.lab12025p.repository.CustomerRepository;
import com.udea.lab12025p.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    // Constructor Injection Replaced Field Injection
    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        // Validar que los numeros de cuenta no sean nulos
        if (transactionDTO.getSenderAccountNumber() == null || transactionDTO.getReceiverAccountNumber() == null) {
            throw new IllegalArgumentException("Los numeros de cuenta del remitente y receptor son obligatorios");
        }

        // Validar que el monto sea mayor a cero
        if (transactionDTO.getAmount() == null
                || transactionDTO.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a transferir debe ser mayor a cero");
        }

        // Validar autotransferencia
        if (transactionDTO.getSenderAccountNumber().equals(transactionDTO.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("No puede transferir dinero a su propia cuenta");
        }

        // Buscar los clientes por numero de cuenta con bloqueo
        Customer sender = customerRepository.findByAccountNumberForUpdate(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del remitente no existe"));
        Customer receiver = customerRepository.findByAccountNumberForUpdate(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("La cuenta del receptor no existe"));
        // Validar que el remitente tenga saldo suficiente
        if (sender.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new IllegalArgumentException("El remitente no tiene saldo suficiente");
        }
        // Realizar la transferencia
        sender.setBalance(sender.getBalance().subtract(transactionDTO.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.getAmount()));

        // Guardar los cambios en las cuentas
        customerRepository.save(sender);
        customerRepository.save(receiver);

        // Crear y guardar la transaccion
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(transactionDTO.getAmount());

        transaction = transactionRepository.save(transaction);

        // Devolver la transaccion creada como DTO
        TransactionDTO savedTransaction = new TransactionDTO();
        savedTransaction.setId(transaction.getId());
        savedTransaction.setSenderAccountNumber(transaction.getSenderAccountNumber());
        savedTransaction.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setTransactionDate(transaction.getTransactionDate());

        return savedTransaction;
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        List<Transaction> transactions = transactionRepository
                .findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);
        return transactions.stream().map(transaction -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(transaction.getId());
            dto.setSenderAccountNumber(transaction.getSenderAccountNumber());
            dto.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
            dto.setAmount(transaction.getAmount());
            dto.setTransactionDate(transaction.getTransactionDate());
            return dto;
        }).toList();
    }
}
