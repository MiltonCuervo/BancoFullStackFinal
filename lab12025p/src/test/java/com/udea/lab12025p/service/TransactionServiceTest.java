package com.udea.lab12025p.service;

import com.udea.lab12025p.DTO.TransactionDTO;
import com.udea.lab12025p.entity.Customer;
import com.udea.lab12025p.entity.Transaction;
import com.udea.lab12025p.repository.CustomerRepository;
import com.udea.lab12025p.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Customer sender;
    private Customer receiver;
    private TransactionDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Datos de prueba iniciales
        sender = new Customer();
        sender.setId(1L);
        sender.setAccountNumber("1111");
        sender.setBalance(new BigDecimal("500.00"));

        receiver = new Customer();
        receiver.setId(2L);
        receiver.setAccountNumber("2222");
        receiver.setBalance(new BigDecimal("100.00"));

        requestDTO = new TransactionDTO();
        requestDTO.setSenderAccountNumber("1111");
        requestDTO.setReceiverAccountNumber("2222");
        requestDTO.setAmount(new BigDecimal("100.00"));
    }

    // TEST 1: Saldo Insuficiente
    @Test
    void testTransferMoney_InsufficientFunds() {
        requestDTO.setAmount(new BigDecimal("600.00")); // Remitente solo tiene 500

        when(customerRepository.findByAccountNumberForUpdate("1111")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumberForUpdate("2222")).thenReturn(Optional.of(receiver));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferMoney(requestDTO);
        });

        assertEquals("El remitente no tiene saldo suficiente", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // TEST 2: Monto Negativo o Cero (Caso Borde descubierto sin protección previa)
    @Test
    void testTransferMoney_NegativeOrZeroAmount() {
        requestDTO.setAmount(new BigDecimal("-50.00"));

        // Comprobamos que esto debe fallar rápido sin consultar bases de datos
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferMoney(requestDTO);
        });

        assertEquals("El monto a transferir debe ser mayor a cero", exception.getMessage());
        verify(customerRepository, never()).findByAccountNumberForUpdate(anyString());
    }

    // TEST 3: Misma cuenta (Autotransferencia)
    @Test
    void testTransferMoney_SameAccount() {
        requestDTO.setReceiverAccountNumber("1111");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferMoney(requestDTO);
        });

        assertEquals("No puede transferir dinero a su propia cuenta", exception.getMessage());
        verify(customerRepository, never()).findByAccountNumberForUpdate(anyString());
    }

    // TEST 4: Cuenta del remitente no existe
    @Test
    void testTransferMoney_AccountNotFound() {
        when(customerRepository.findByAccountNumberForUpdate("1111")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferMoney(requestDTO);
        });

        assertEquals("La cuenta del remitente no existe", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // TEST 5: Cuenta del receptor no existe
    @Test
    void testTransferMoney_ReceiverAccountNotFound() {
        when(customerRepository.findByAccountNumberForUpdate("1111"))
            .thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumberForUpdate("2222"))
            .thenReturn(Optional.empty()); // receptor no existe

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> {
                transactionService.transferMoney(requestDTO);
            }
        );

        assertEquals("La cuenta del receptor no existe", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // TEST 6: Camino Feliz
    @Test
    void testTransferMoney_SuccessPath() {
        when(customerRepository.findByAccountNumberForUpdate("1111")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumberForUpdate("2222")).thenReturn(Optional.of(receiver));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(99L);
        mockTransaction.setSenderAccountNumber("1111");
        mockTransaction.setReceiverAccountNumber("2222");
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        TransactionDTO result = transactionService.transferMoney(requestDTO);

        assertNotNull(result.getId());
        assertEquals("1111", result.getSenderAccountNumber());
        assertEquals("2222", result.getReceiverAccountNumber());
        assertEquals(new BigDecimal("400.00"), sender.getBalance()); // 500 - 100
        assertEquals(new BigDecimal("200.00"), receiver.getBalance()); // 100 + 100

        verify(customerRepository, times(2)).save(any(Customer.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
