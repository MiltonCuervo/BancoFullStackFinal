package com.udea.lab12025p.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udea.lab12025p.dto.TransactionDTO;
import com.udea.lab12025p.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TransactionDTO sampleTransaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
        // Necesario para serializar LocalDateTime correctamente
        objectMapper.registerModule(new JavaTimeModule());

        sampleTransaction = new TransactionDTO();
        sampleTransaction.setId(1L);
        sampleTransaction.setSenderAccountNumber("1111");
        sampleTransaction.setReceiverAccountNumber("2222");
        sampleTransaction.setAmount(new BigDecimal("100.00"));
        sampleTransaction.setTransactionDate(LocalDateTime.now());
    }

    // TEST 1: POST transferencia exitosa
    @Test
    void testTransferMoney_Success_ReturnsOk() throws Exception {
        when(transactionService.transferMoney(any(TransactionDTO.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderAccountNumber").value("1111"));
    }

    // TEST 2: POST con fondos insuficientes retorna 400
    @Test
    void testTransferMoney_InsufficientFunds_ReturnsBadRequest() throws Exception {
        when(transactionService.transferMoney(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("El remitente no tiene saldo suficiente"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTransaction)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El remitente no tiene saldo suficiente"));
    }

    // TEST 3: GET historial de transacciones de una cuenta
    @Test
    void testGetTransactionsByAccount_ReturnsOk() throws Exception {
        when(transactionService.getTransactionsForAccount("1111")).thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/api/transactions/1111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderAccountNumber").value("1111"));
    }

    // TEST 4: GET cuenta sin historial retorna lista vacía
    @Test
    void testGetTransactionsByAccount_EmptyList_ReturnsOk() throws Exception {
        when(transactionService.getTransactionsForAccount("9999")).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/9999"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
